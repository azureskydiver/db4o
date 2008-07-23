/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class FirstClassObjectHandler  implements FieldAwareTypeHandler {
    
    private static final int HASHCODE_FOR_NULL = 72483944; 
    
    private ClassMetadata _classMetadata;

    public FirstClassObjectHandler(ClassMetadata classMetadata) {
        _classMetadata = classMetadata;
    }
    
    public FirstClassObjectHandler(){
        
    }

    public void defragment(final DefragmentContext context) {
        TraverseAspectCommand command = new TraverseAspectCommand() {
            public int fieldCount(ClassMetadata classMetadata, ByteArrayBuffer reader) {
                return context.readInt();
            }
            public void processAspect(ClassAspect aspect, boolean isNull, ClassMetadata containingClass) {
                if (!isNull) {
                    aspect.defragAspect(context);
                } 
            }
        };
        traverseAllAspects(context, command);
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        context.deleteObject();
    }

    public final void instantiateAspects(final UnmarshallingContext context) {
        
        final BooleanByRef updateFieldFound = new BooleanByRef();
        
        ContextState savedState = context.saveState();
        
        TraverseAspectCommand command = new TraverseAspectCommand() {
            public void processAspect(ClassAspect aspect, boolean isNull, ClassMetadata containingClass) {
                if(aspect instanceof FieldMetadata){
                    FieldMetadata field = (FieldMetadata) aspect;
                    if(field.updating()){
                        updateFieldFound.value = true;
                    }
                    if (isNull) {
                        field.set(context.persistentObject(), null);
                        return;
                    }
                }
                aspect.instantiate(context);
            }
        };
        traverseAllAspects(context, command);
        
        if(updateFieldFound.value){
            context.restoreState(savedState);
            command = new TraverseFieldCommand() {
                public void processAspect(ClassAspect aspect, boolean isNull, ClassMetadata containingClass) {
                    if (! isNull) {
                        ((FieldMetadata)aspect).attemptUpdate(context);
                    }
                }
            };
            traverseAllAspects(context, command);
        }
        
    }
    
    public Object read(ReadContext context) {
        UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context;
        
// FIXME: Commented out code below is the implementation plan to let
//        FirstClassObjectHandler take responsibility of fieldcount
//        and null Bitmap.        
       
        
//        BitMap4 nullBitMap = unmarshallingContext.readBitMap(fieldCount);
//        int fieldCount = context.readInt();

        instantiateAspects(unmarshallingContext);
        
        return unmarshallingContext.persistentObject();
    }

    public void write(final WriteContext context, Object obj) {

//        int fieldCount = _classMetadata.fieldCount();
//        context.writeInt(fieldCount);
//        final BitMap4 nullBitMap = new BitMap4(fieldCount);
//        ReservedBuffer bitMapBuffer = context.reserve(nullBitMap.marshalledLength());
        
        marshallAspects(obj, (MarshallingContext)context);
        
//        bitMapBuffer.writeBytes(nullBitMap.bytes());
        
    }
    
    public void marshallAspects(final Object obj, final MarshallingContext context) {
       final Transaction trans = context.transaction();
        TraverseAspectCommand command = new TraverseAspectCommand() {
             
            public int fieldCount(ClassMetadata classMetadata, ByteArrayBuffer buffer) {
                int fieldCount = classMetadata._aspects.length;
                context.fieldCount(fieldCount);
                return fieldCount;
            }
            public void processAspect(ClassAspect aspect, boolean isNull, ClassMetadata containingClass) {
                if(! aspect.enabled(context)){
                	context.isNull(context.currentSlot(), true);
                	return;
                }
                Object marshalledObject = obj;
                if(aspect instanceof FieldMetadata){
                    FieldMetadata field = (FieldMetadata) aspect;
                    marshalledObject = field.getOrCreate(trans, obj);
                    if(marshalledObject == null) {
                        context.isNull(context.currentSlot(), true);
                        field.addIndexEntry(trans, context.objectID(), null);
                        return;
                    }
                    if (marshalledObject instanceof Db4oTypeImpl) {
                    	marshalledObject = ((Db4oTypeImpl) marshalledObject).storedTo(trans);
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
        int id = 0;
        ReflectClass claxx = null;
        if(source instanceof Integer){
            id = ((Integer)source).intValue();
        } else if(source instanceof TransactionContext){
            TransactionContext tc = (TransactionContext)source;
            Object obj = tc._object;
            id = _classMetadata.stream().getID(tc._transaction, obj);
            claxx = _classMetadata.reflector().forObject(obj);
        }else{
            throw new IllegalComparisonException();
        }
        return new ClassMetadata.PreparedComparisonImpl(id, claxx);
    }
    
    public abstract static class TraverseAspectCommand {
        
        private boolean _cancelled=false;
        
        public int fieldCount(ClassMetadata classMetadata, ByteArrayBuffer reader) {
            return classMetadata.readFieldCount(reader);
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
        

        public abstract void processAspect(ClassAspect aspect,boolean isNull, ClassMetadata containingClass);
    }
    
    public abstract static class TraverseFieldCommand extends TraverseAspectCommand{
        
        public boolean accept(ClassAspect aspect){
            return aspect instanceof FieldMetadata;
        }
    }
    
    protected final void traverseAllAspects(MarshallingInfo context, TraverseAspectCommand command) {
        ClassMetadata classMetadata = classMetadata();
        while(classMetadata != null){
            traverseDeclaredAspects(context, classMetadata, command);
            if(command.cancelled()){
                return;
            }
            classMetadata = classMetadata.i_ancestor;
        }
    }
    
    private void traverseDeclaredAspects(MarshallingInfo context, ClassMetadata classMetadata,
        TraverseAspectCommand command) {
        int fieldCount=command.fieldCount(classMetadata, ((ByteArrayBuffer)context.buffer()));
        context.aspectCount(fieldCount);
        for (int i = 0; i < fieldCount && !command.cancelled(); i++) {
            if(command.accept(classMetadata._aspects[i])){
                command.processAspect(
                		classMetadata._aspects[i],
                		isNull(context,context.currentSlot()),
                		classMetadata);
            }
            context.beginSlot();
        }
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
        if(! (obj instanceof FirstClassObjectHandler)){
            return false;
        }
        FirstClassObjectHandler other = (FirstClassObjectHandler) obj;
        if(_classMetadata == null){
            return other._classMetadata == null;
        }
        return _classMetadata.equals(other._classMetadata);
    }
    
    public int hashCode() {
        if(_classMetadata != null){
            return _classMetadata.hashCode();
        }
        return HASHCODE_FOR_NULL;
    }
    
    public TypeHandler4 unversionedTemplate() {
        return new FirstClassObjectHandler(null);
    }

    public Object deepClone(Object context) {
        TypeHandlerCloneContext typeHandlerCloneContext = (TypeHandlerCloneContext) context;
        FirstClassObjectHandler cloned = (FirstClassObjectHandler) Reflection4.newInstance(this);
        if(typeHandlerCloneContext.original instanceof FirstClassObjectHandler){
            FirstClassObjectHandler original = (FirstClassObjectHandler) typeHandlerCloneContext.original;
            cloned._classMetadata = original._classMetadata;
        }else{
            if(_classMetadata == null){
                throw new IllegalStateException();
            }
            cloned._classMetadata = _classMetadata;
        }
        return cloned;
    }
    
    public void collectIDs(final CollectIdContext context, final String fieldName) {
        TraverseAspectCommand command = new TraverseAspectCommand() {
            public void processAspect(ClassAspect aspect, boolean isNull, ClassMetadata containingClass) {
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

    public void cascadeActivation(ActivationContext4 context) {
        context.cascadeActivationToTarget(classMetadata(), classMetadata().descendOnCascadingActivation());
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        if (classMetadata().isArray()) {
            return classMetadata();
        }
        return null;
    }

    public void collectIDs(final QueryingReadContext context) throws Db4oIOException {

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
            public void processAspect(ClassAspect aspect, boolean isNull, ClassMetadata containingClass) {
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
        TraverseAspectCommand command = new TraverseFieldCommand() {
            public void processAspect(ClassAspect aspect, boolean isNull, ClassMetadata containingClass) {
                FieldMetadata field = (FieldMetadata)aspect;
                if (isNull) {
                    field.addIndexEntry(context.transaction(), context.id(), null);
                } else {
                    field.addFieldIndex(context, oldSlot);
                }
            }
        };
        traverseAllAspects(context, command);
    }
    
    public void deleteMembers(final DeleteContextImpl context, final boolean isUpdate) {
        TraverseAspectCommand command=new TraverseAspectCommand() {
            public void processAspect(ClassAspect aspect, boolean isNull, ClassMetadata containingClass) {
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

    public boolean seekToField(final ObjectHeaderContext context, final FieldMetadata field) {
        final BooleanByRef found = new BooleanByRef(false);
        TraverseAspectCommand command=new TraverseAspectCommand() {
            public void processAspect(ClassAspect curField, boolean isNull, ClassMetadata containingClass) {
                if (curField == field) {
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

}
