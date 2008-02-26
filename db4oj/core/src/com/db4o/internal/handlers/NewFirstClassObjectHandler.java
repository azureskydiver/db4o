/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class NewFirstClassObjectHandler  implements TypeHandler4 {
    
    private final ClassMetadata _classMetadata;

    public NewFirstClassObjectHandler(ClassMetadata classMetadata) {
        _classMetadata = classMetadata;
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
        ((ObjectContainerBase)context.objectContainer()).deleteByID(
                context.transaction(), context.readInt(), context.cascadeDeleteDepth());
    }

    public Object read(ReadContext context) {
        UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context;
        instantiateFields(unmarshallingContext);
        return unmarshallingContext.persistentObject();
    }
    
    public final void instantiateFields(final UnmarshallingContext context) {
        
        final BooleanByRef updateFieldFound = new BooleanByRef();
        
        int savedOffset = context.offset();
        
        TraverseFieldCommand command = new TraverseFieldCommand() {
            public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                if(field.updating()){
                    updateFieldFound.value = true;
                }
                if (isNull) {
                    field.set(context.persistentObject(), null);
                    return;
                } 
                boolean ok = false;
                try {
                    field.instantiate(context);
                    ok = true;
                } finally {
                    if(!ok) {
                        cancel();
                    }
                }
            }
        };
        traverseFields(context, command);
        
        if(updateFieldFound.value){
            context.seek(savedOffset);
            command = new TraverseFieldCommand() {
                public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                    field.attemptUpdate(context);
                }
            };
            traverseFields(context, command);
        }
        
    }

    public void write(WriteContext context, Object obj) {
        marshall(obj, (MarshallingContext)context);
    }
    
    public void marshall(final Object obj, final MarshallingContext context) {
       final Transaction trans = context.transaction();
        TraverseFieldCommand command = new TraverseFieldCommand() {
            private int fieldIndex = -1; 
            public int fieldCount(ClassMetadata classMetadata, ByteArrayBuffer buffer) {
                int fieldCount = classMetadata.i_fields.length;
                context.fieldCount(fieldCount);
                return fieldCount;
            }
            public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                context.nextField();
                fieldIndex++;
                Object child = field.getOrCreate(trans, obj);
                if(child == null) {
                    context.isNull(fieldIndex, true);
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


    public PreparedComparison prepareComparison(Object source) {
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
        traverseFields(context.classMetadata(), (ByteArrayBuffer)context.buffer(), context, command);
    }
    
    protected final void traverseFields(ClassMetadata classMetadata, ByteArrayBuffer buffer, FieldListInfo fieldList,TraverseFieldCommand command) {
        int fieldIndex=0;
        while(classMetadata!=null&&!command.cancelled()) {
            int fieldCount=command.fieldCount(classMetadata, buffer);
            for (int i = 0; i < fieldCount && !command.cancelled(); i++) {
                command.processField(classMetadata.i_fields[i],isNull(fieldList,fieldIndex),classMetadata);
                fieldIndex ++;
            }
            classMetadata=classMetadata.i_ancestor;
        }
    }
    
    protected boolean isNull(FieldListInfo fieldList,int fieldIndex) {
        return fieldList.isNull(fieldIndex);
    }

    public ClassMetadata classMetadata() {
        return _classMetadata;
    }

}
