/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class FirstClassObjectHandler  implements TypeHandler4, CompositeTypeHandler, CollectIdHandler, FirstClassHandler {
    
    private static final int HASHCODE_FOR_NULL = 72483944; 
    
    private ClassMetadata _classMetadata;

    public FirstClassObjectHandler(ClassMetadata classMetadata) {
        _classMetadata = classMetadata;
    }
    
    public FirstClassObjectHandler(){
        
    }

    public void defragment(DefragmentContext context) {
        if(_classMetadata.hasClassIndex()) {
            context.copyID();
        }
        else {
            context.copyUnindexedID();
        }
        int restLength = (_classMetadata.linkLength()-Const4.INT_LENGTH);
        context.incrementOffset(restLength);
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        context.deleteObject();
    }

    public final void instantiateFields(final UnmarshallingContext context) {
        
        final BooleanByRef updateFieldFound = new BooleanByRef();
        
        ContextState savedState = context.saveState();
        
        TraverseFieldCommand command = new TraverseFieldCommand() {
            public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                if(field.updating()){
                    updateFieldFound.value = true;
                }
                if (isNull) {
                    field.set(context.persistentObject(), null);
                    return;
                } 
                field.instantiate(context);
            }
        };
        traverseFields(context, command);
        
        if(updateFieldFound.value){
            context.restoreState(savedState);
            command = new TraverseFieldCommand() {
                public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                    if (! isNull) {
                        field.attemptUpdate(context);
                    }
                }
            };
            traverseFields(context, command);
        }
        
    }
    
    public Object read(ReadContext context) {
        UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context;
        
// FIXME: Commented out code below is the implementation plan to let
//        FirstClassObjectHandler take responsibility of fieldcount
//        and null Bitmap.        
       
        
//        BitMap4 nullBitMap = unmarshallingContext.readBitMap(fieldCount);
//        int fieldCount = context.readInt();

        instantiateFields(unmarshallingContext);
        
        if(classMetadata().i_ancestor != null){
            classMetadata().i_ancestor.read(context);
        }

        return unmarshallingContext.persistentObject();
    }

    public void write(final WriteContext context, Object obj) {

//        int fieldCount = _classMetadata.fieldCount();
//        context.writeInt(fieldCount);
//        final BitMap4 nullBitMap = new BitMap4(fieldCount);
//        ReservedBuffer bitMapBuffer = context.reserve(nullBitMap.marshalledLength());
        
        marshall(obj, (MarshallingContext)context);
        
//        bitMapBuffer.writeBytes(nullBitMap.bytes());
        
        if(classMetadata().i_ancestor != null){
            classMetadata().i_ancestor.write(context, obj);
        }
        
    }
    
    public void marshall(final Object obj, final MarshallingContext context) {
       final Transaction trans = context.transaction();
        TraverseFieldCommand command = new TraverseFieldCommand() {
             
            public int fieldCount(ClassMetadata classMetadata, ByteArrayBuffer buffer) {
                int fieldCount = classMetadata.i_fields.length;
                context.fieldCount(fieldCount);
                return fieldCount;
            }
            public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                Object child = field.getOrCreate(trans, obj);
                if(child == null) {
                    context.isNull(context.currentSlot(), true);
                    field.addIndexEntry(trans, context.objectID(), null);
                    return;
                }
                
                if (child instanceof Db4oTypeImpl) {
                    child = ((Db4oTypeImpl) child).storedTo(trans);
                }
                field.marshall(context, child);
            }
        };
        traverseFields(context, command);
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
    
    protected abstract static class TraverseFieldCommand {
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

        public abstract void processField(FieldMetadata field,boolean isNull, ClassMetadata containingClass);
    }
    
    protected final void traverseFields(MarshallingInfo context, TraverseFieldCommand command) {
        int fieldCount=command.fieldCount(classMetadata(), ((ByteArrayBuffer)context.buffer()));
        for (int i = 0; i < fieldCount && !command.cancelled(); i++) {
            command.processField(classMetadata().i_fields[i],isNull(context,context.currentSlot()),classMetadata());
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
    
    public TypeHandler4 genericTemplate() {
        return new FirstClassObjectHandler(null);
    }

    public Object deepClone(Object context) {
        TypeHandlerCloneContext typeHandlerCloneContext = (TypeHandlerCloneContext) context;
        FirstClassObjectHandler cloned = (FirstClassObjectHandler) Reflection4.newInstance(this);
        FirstClassObjectHandler original = (FirstClassObjectHandler) typeHandlerCloneContext.original;
        cloned._classMetadata = original._classMetadata;
        return cloned;
    }

    
    public void collectIDs(final CollectIdContext context) {
        TraverseFieldCommand command = new TraverseFieldCommand() {
            public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                if(isNull) {
                    return;
                }
                if (context.fieldName().equals(field.getName())) {
                    field.collectIDs(context);
                } 
                else {
                    field.incrementOffset(context);
                }
            }
        };
        traverseFields(context, command);
        if(classMetadata().i_ancestor != null){
            classMetadata().i_ancestor.collectIDs(context);
        }
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

    public void readCandidates(final QueryingReadContext context) throws Db4oIOException {

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
            
        final QCandidates candidates = context.candidates();

        // FIXME: [TA] review activation depth
        
        container.activate(transaction, obj, container.activationDepthProvider().activationDepth(2, ActivationMode.ACTIVATE));
        Platform4.forEachCollectionElement(obj, new Visitor4() {
            public void visit(Object elem) {
                candidates.addByIdentity(new QCandidate(candidates, elem, container.getID(transaction, elem), true));
            }
        });
        
    }

}
