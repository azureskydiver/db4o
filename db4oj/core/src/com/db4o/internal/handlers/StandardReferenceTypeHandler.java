/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class StandardReferenceTypeHandler implements FieldAwareTypeHandler, IndexableTypeHandler, ReadsObjectIds {
    
    private static final int HASHCODE_FOR_NULL = 72483944; 
    
    private ClassMetadata _classMetadata;

    public StandardReferenceTypeHandler(ClassMetadata classMetadata) {
        classMetadata(classMetadata);
    }
    
    public StandardReferenceTypeHandler(){
    }

    public void defragment(final DefragmentContext context) {
        traverseAllAspects(context, new TraverseAspectCommand() {
        	
            @Override
            public int aspectCount(ClassMetadata classMetadata, ByteArrayBuffer reader) {
                return context.readInt();
            }
            
            @Override
            public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
                if (!isNull) {
                    aspect.defragAspect(context);
                } 
            }
            
            @Override
            public boolean accept(ClassAspect aspect) {
            	return aspect.isEnabledOn(context);
            }
        });
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        context.deleteObject();
    }

    public final void activateAspects(final UnmarshallingContext context) {
    	
        final BooleanByRef schemaUpdateDetected = new BooleanByRef();
        
        ContextState savedState = context.saveState();
        
        TraverseAspectCommand command = new TraverseAspectCommand() {
        	
        	@Override
        	public boolean accept(ClassAspect aspect) {
        		return aspect.isEnabledOn(context);
        	}
        	
            public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
            	
			    if(aspect instanceof FieldMetadata){
                    FieldMetadata field = (FieldMetadata) aspect;
                    if(field.updating()){
                        schemaUpdateDetected.value = true;
                    }
                    // TODO: cant the aspect handle it itself?
                    // Probably no because old aspect versions might not be able
                    // to handle null...
                    if (isNull) {
                        field.set(context.persistentObject(), null);
                        return;
                    }
                }
			    

                aspect.activate(context);
            }
        };
        traverseAllAspects(context, command);
        
        if(schemaUpdateDetected.value){
            context.restoreState(savedState);
            command = new TraverseFieldCommand() {
                public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
                    if (! isNull) {
                        ((FieldMetadata)aspect).attemptUpdate(context);
                    }
                }
            };
            traverseAllAspects(context, command);
        }
        
    }
    
    public void activate(ReferenceActivationContext context) {
        activateAspects((UnmarshallingContext) context);
    }

	public void write(WriteContext context, Object obj) {
        marshallAspects(obj, (MarshallingContext)context);
    }
    
    public void marshallAspects(final Object obj, final MarshallingContext context) {
    	final Transaction trans = context.transaction();
        final TraverseAspectCommand command = new TraverseAspectCommand() {
            public int aspectCount(ClassMetadata classMetadata, ByteArrayBuffer buffer) {
                int fieldCount = classMetadata._aspects.length;
                context.fieldCount(fieldCount);
                return fieldCount;
            }
            
            @Override
            public boolean accept(ClassAspect aspect) {
            	return aspect.isEnabledOn(context);
            }
            
            public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
               
            	Object marshalledObject = obj;
                if(aspect instanceof FieldMetadata){
                    FieldMetadata field = (FieldMetadata) aspect;
                    marshalledObject = field.getOrCreate(trans, obj);
                    if(marshalledObject == null) {
                        context.isNull(currentSlot, true);
                        field.addIndexEntry(trans, context.objectID(), null);
                        return;
                    }
                }
                
                aspect.marshall(context, marshalledObject);
            }
        };
        traverseAllAspects(context, command);
    }


    public PreparedComparison prepareComparison(Context context, Object source) {
        if(source == null){
            return new PreparedComparison() {
                public int compareTo(Object obj) {
                    if(obj == null){
                        return 0;
                    }
                    return -1;
                }
            };
        }
        
        if(source instanceof Integer){
            int id = ((Integer)source).intValue();
            return new PreparedComparisonImpl(id, null);
        } 
        
        if(source instanceof TransactionContext){
            TransactionContext tc = (TransactionContext)source;
            Object obj = tc._object;
            Transaction transaction = tc._transaction;
			int id = idFor(obj, transaction);
            return new PreparedComparisonImpl(id, reflectClassFor(obj));
        }
        
        throw new IllegalComparisonException();
    }

	private ReflectClass reflectClassFor(Object obj) {
		return classMetadata().reflector().forObject(obj);
	}

	private int idFor(Object object, Transaction inTransaction) {
		return stream().getID(inTransaction, object);
	}

	private ObjectContainerBase stream() {
		return classMetadata().container();
	}
    
    public abstract static class TraverseAspectCommand {
        
        private boolean _cancelled=false;
        
        public int aspectCount(ClassMetadata classMetadata, ByteArrayBuffer reader) {
            return classMetadata.readAspectCount(reader);
        }

        public boolean cancelled() {
            return _cancelled;
        }
        
        protected void cancel() {
            _cancelled=true;
        }
        
        public boolean accept(ClassAspect aspect){
            return true;
        }
        

        public abstract void processAspect(ClassAspect aspect,int currentSlot, boolean isNull, ClassMetadata containingClass);
    }
    
    public abstract static class TraverseFieldCommand extends TraverseAspectCommand{
        
        public boolean accept(ClassAspect aspect){
            return aspect instanceof FieldMetadata;
        }
    }
    
    public final static class PreparedComparisonImpl implements PreparedComparison {
		
		private final int _id;
		
		private final ReflectClass _claxx;
	
		public PreparedComparisonImpl(int id, ReflectClass claxx) {
			_id = id;
			_claxx = claxx;
		}
	
		public int compareTo(Object obj) {
		    if(obj instanceof TransactionContext){
		        obj = ((TransactionContext)obj)._object;
		    }
		    if(obj == null){
		    	return _id == 0 ? 0 : 1;
		    }
		    if(obj instanceof Integer){
				int targetInt = ((Integer)obj).intValue();
				return _id == targetInt ? 0 : (_id < targetInt ? - 1 : 1); 
		    }
		    if(_claxx != null){
		    	if(_claxx.isAssignableFrom(_claxx.reflector().forObject(obj))){
		    		return 0;
		    	}
		    }
		    throw new IllegalComparisonException();
		}
	}

	protected final void traverseAllAspects(MarshallingInfo context, TraverseAspectCommand command) {
    	int currentSlot = 0;
        
    	ClassMetadata classMetadata = classMetadata();
        assertClassMetadata(context.classMetadata());
        
        while(classMetadata != null){
            int aspectCount=command.aspectCount(classMetadata, ((ByteArrayBuffer)context.buffer()));
			context.aspectCount(aspectCount);
			for (int i = 0; i < aspectCount && !command.cancelled(); i++) {
			    if(command.accept(classMetadata._aspects[i])){
			        command.processAspect(
			        		classMetadata._aspects[i],
			        		currentSlot,
			        		isNull(context, currentSlot), classMetadata);
			    }
			    context.beginSlot();
			    currentSlot++;
			}
            if(command.cancelled()){
                return;
            }
            classMetadata = classMetadata.i_ancestor;
        }
    }

	private void assertClassMetadata(final ClassMetadata contextMetadata) {
//		if (contextMetadata != classMetadata()) {
//        	throw new IllegalStateException("expecting '" + classMetadata() + "', got '" + contextMetadata + "'");
//        }
	}
    
    protected boolean isNull(FieldListInfo fieldList,int fieldIndex) {
        return fieldList.isNull(fieldIndex);
    }

    public ClassMetadata classMetadata() {
        return _classMetadata;
    }
    
    public void classMetadata(ClassMetadata classMetadata) {
        _classMetadata = classMetadata;
    }
    
    public boolean equals(Object obj) {
        if(! (obj instanceof StandardReferenceTypeHandler)){
            return false;
        }
        StandardReferenceTypeHandler other = (StandardReferenceTypeHandler) obj;
        if(classMetadata() == null){
            return other.classMetadata() == null;
        }
        return classMetadata().equals(other.classMetadata());
    }
    
    public int hashCode() {
        if(classMetadata() != null){
            return classMetadata().hashCode();
        }
        return HASHCODE_FOR_NULL;
    }
    
    public TypeHandler4 unversionedTemplate() {
        return new StandardReferenceTypeHandler(null);
    }

    public Object deepClone(Object context) {
        TypeHandlerCloneContext typeHandlerCloneContext = (TypeHandlerCloneContext) context;
        StandardReferenceTypeHandler cloned = (StandardReferenceTypeHandler) Reflection4.newInstance(this);
        if(typeHandlerCloneContext.original instanceof StandardReferenceTypeHandler){
            StandardReferenceTypeHandler original = (StandardReferenceTypeHandler) typeHandlerCloneContext.original;
            cloned.classMetadata(original.classMetadata());
        }else{

        	// New logic: ClassMetadata takes the responsibility in 
        	//           #correctHandlerVersion() to set the 
        	//           ClassMetadata directly on cloned handler.
        	
//            if(_classMetadata == null){
//                throw new IllegalStateException();
//            }
        	
            cloned.classMetadata(_classMetadata);
        }
        return cloned;
    }
    
    public void collectIDs(final CollectIdContext context, final String fieldName) {
        TraverseAspectCommand command = new TraverseAspectCommand() {
            public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
                if(isNull) {
                    return;
                }
                if (fieldName.equals(aspect.getName())) {
                    aspect.collectIDs(context);
                } 
                else {
                    aspect.incrementOffset(context);
                }
            }
        };
        traverseAllAspects(context, command);
    }

    public void cascadeActivation(ActivationContext context) {
    	assertClassMetadata(context.classMetadata());
        context.cascadeActivationToTarget();
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
    	if (classMetadata().isArray()) {
    		return this;
    	}
    	return null;
    }

    public void collectIDs(final QueryingReadContext context) throws Db4oIOException {
    	if(collectIDsByTypehandlerAspect(context)){
    		return;
    	}
    	collectIDsByInstantiatingCollection(context);
    }
    
    private boolean collectIDsByTypehandlerAspect(final QueryingReadContext context) throws Db4oIOException {
    	final BooleanByRef aspectFound = new BooleanByRef(false);
    	final CollectIdContext subContext =  CollectIdContext.forID(context.transaction(), context.collector(), context.collectionID());
        TraverseAspectCommand command = new TraverseAspectCommand() {
            public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
                if(isNull) {
                    return;
                }
                if(isCollectIdTypehandlerAspect(aspect)){
                	aspectFound.value = true;
                	aspect.collectIDs(subContext);
                }else {
                	aspect.incrementOffset(subContext);
                }

            }
        };
        traverseAllAspects(subContext, command);
        return aspectFound.value;
    }
    
    private boolean isCollectIdTypehandlerAspect(ClassAspect aspect){
    	if(! (aspect instanceof TypeHandlerAspect)){
    		return false;
    	}
    	TypeHandler4 typehandler = ((TypeHandlerAspect)aspect)._typeHandler;
    	return  Handlers4.isCascading(typehandler);
    }
    
    private void collectIDsByInstantiatingCollection(final QueryingReadContext context) throws Db4oIOException {
        int id = context.collectionID();
        if (id == 0) {
            return;
        }
        final Transaction transaction = context.transaction();
        final ObjectContainerBase container = context.container();
        Object obj = container.getByID(transaction, id);
        if (obj == null) {
            return;
        }

        // FIXME: [TA] review activation depth
        int depth = classMetadata().adjustDepthToBorders(2);
        container.activate(transaction, obj, container.activationDepthProvider().activationDepth(depth, ActivationMode.ACTIVATE));
        Platform4.forEachCollectionElement(obj, new Visitor4() {
            public void visit(Object elem) {
                context.add(elem);
            }
        });
    }
    
    public void readVirtualAttributes(final ObjectReferenceContext context){
        TraverseAspectCommand command = new TraverseAspectCommand() {
            public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
                if (!isNull) {
                    if(aspect instanceof VirtualFieldMetadata){
                        ((VirtualFieldMetadata)aspect).readVirtualAttribute(context);
                    } else {
                        aspect.incrementOffset(context);
                    }
                }
            }
        };
        traverseAllAspects(context, command);
    }

    public void addFieldIndices(final ObjectIdContextImpl context, final Slot oldSlot) {
        TraverseAspectCommand command = new TraverseAspectCommand() {
            public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
            	if(aspect instanceof FieldMetadata){
	                FieldMetadata field = (FieldMetadata)aspect;
	                if (isNull) {
	                    field.addIndexEntry(context.transaction(), context.id(), null);
	                } else {
	                    field.addFieldIndex(context, oldSlot);
	                }
            	}else{
            		aspect.incrementOffset(context.buffer());
            	}
            }
        };
        traverseAllAspects(context, command);
    }
    
    public void deleteMembers(final DeleteContextImpl context, final boolean isUpdate) {
        TraverseAspectCommand command=new TraverseAspectCommand() {
            public void processAspect(ClassAspect aspect, int currentSlot, boolean isNull, ClassMetadata containingClass) {
                if(isNull){
                	if(aspect instanceof FieldMetadata){
                		FieldMetadata field = (FieldMetadata)aspect;
                        field.removeIndexEntry(context.transaction(), context.id(), null);
                	}
                	return;
                }
                aspect.delete(context, isUpdate);
            }
        };
        traverseAllAspects(context, command);
    }

    public boolean seekToField(final ObjectHeaderContext context, final ClassAspect aspect) {
        final BooleanByRef found = new BooleanByRef(false);
        TraverseAspectCommand command=new TraverseAspectCommand() {
        	
        	@Override
        	public boolean accept(ClassAspect aspect) {
        		return aspect.isEnabledOn(context);
        	}
        	
            public void processAspect(ClassAspect curField, int currentSlot, boolean isNull, ClassMetadata containingClass) {
                if (curField == aspect) {
                    found.value = !isNull;
                    cancel();
                    return;
                }
                if(!isNull){
                    curField.incrementOffset(context);
                }
            }
        };
        traverseAllAspects(context, command);
        return found.value;
    }

	public boolean canHold(ReflectClass type) {
		return classMetadata().canHold(type);
    }
	
   public final Object indexEntryToObject(Context context, Object indexEntry){
        if(indexEntry == null){
            return null;
        }
        int id = ((Integer)indexEntry).intValue();
        return ((ObjectContainerBase)context.objectContainer()).getByID2(context.transaction(), id);
    }

	public final void defragIndexEntry(DefragmentContextImpl context) {
		context.copyID();
	}	

    public final Object readIndexEntry(ByteArrayBuffer a_reader) {
        return new Integer(a_reader.readInt());
    }
    
    public final Object readIndexEntryFromObjectSlot(MarshallerFamily mf, StatefulBuffer a_writer) throws CorruptionException{
        return readIndexEntry(a_writer);
    }
    
    public Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException{
        return new Integer(context.readInt());
    }
    
    public int linkLength() {
    	return Const4.ID_LENGTH;
    }

    public void writeIndexEntry(ByteArrayBuffer a_writer, Object a_object) {
        
        if(a_object == null){
            a_writer.writeInt(0);
            return;
        }
        
        a_writer.writeInt(((Integer)a_object).intValue());
    }
    
    public TypeHandler4 delegateTypeHandler(Context context){
    	return classMetadata().delegateTypeHandler(context);
    }
    
    public ObjectID readObjectID(InternalReadContext context){
        return ObjectID.read(context);
    }
}
