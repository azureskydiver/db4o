/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;

public abstract class ObjectMarshaller {
    
    public MarshallerFamily _family;
    
	protected abstract static class TraverseFieldCommand {
		private boolean _cancelled=false;
		
		public int fieldCount(ClassMetadata classMetadata, ReadBuffer reader) {
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

    protected abstract boolean isNull(FieldListInfo fieldList,int fieldIndex);

    public abstract void addFieldIndices(
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            StatefulBuffer writer, 
            Slot oldSlot) ;
    
    protected StatefulBuffer createWriterForNew(
            Transaction trans, 
            ObjectReference yo, 
            int updateDepth, 
            int length) {
        
        int id = yo.getID();
        Slot slot = new Slot(-1, length);
        
        if(trans instanceof LocalTransaction){
            slot = ((LocalTransaction)trans).file().getSlot(length);
            trans.slotFreeOnRollback(id, slot);
        }
        trans.setPointer(id, slot);
        return createWriterForUpdate(trans, updateDepth, id, slot.address(), slot.length());
    }

    protected StatefulBuffer createWriterForUpdate(
            Transaction a_trans, 
            int updateDepth, 
            int id, 
            int address, 
            int length) {
        
        length = a_trans.container().blockAlignedBytes(length);
        StatefulBuffer writer = new StatefulBuffer(a_trans, length);
        writer.useSlot(id, address, length);
        if (Deploy.debug) {
            writer.writeBegin(Const4.YAPOBJECT);
        }
        writer.setUpdateDepth(updateDepth);
        return writer;
    }
    
    public abstract void deleteMembers(
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            StatefulBuffer writer, 
            int a_type, 
            boolean isUpdate);
    
    public abstract boolean findOffset(
            ClassMetadata classMetadata, 
            FieldListInfo fieldListInfo, 
            ByteArrayBuffer buffer, 
            FieldMetadata field);
    
    public final void marshallUpdateWrite(
            Transaction trans,
            Pointer4 pointer,
            ObjectReference ref, 
            Object obj, 
            ByteArrayBuffer buffer) {
        
        ClassMetadata classMetadata = ref.classMetadata();
        
        ObjectContainerBase container = trans.container();
        container.writeUpdate(trans, pointer, classMetadata, buffer);
        if (ref.isActive()) {
            ref.setStateClean();
        }
        ref.endProcessing();
        objectOnUpdate(trans, classMetadata, obj);
    }

	private void objectOnUpdate(Transaction transaction, ClassMetadata yc, Object obj) {
		ObjectContainerBase container = transaction.container();
		container.callbacks().objectOnUpdate(transaction, obj);
		yc.dispatchEvent(transaction, obj, EventDispatcher.UPDATE);
	}
    
    public abstract Object readIndexEntry(
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            FieldMetadata yf, 
            StatefulBuffer reader);

    public abstract ObjectHeaderAttributes readHeaderAttributes(ByteArrayBuffer reader);
    
    public abstract void readVirtualAttributes(
            Transaction trans,  
            ClassMetadata yc, 
            ObjectReference yo, 
            ObjectHeaderAttributes attributes, 
            ByteArrayBuffer reader);

	public abstract void writeObjectClassID(ByteArrayBuffer reader,int id);
	
	public abstract void skipMarshallerInfo(ByteArrayBuffer reader);

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
    
    public void marshall(final Object obj, final MarshallingContext context) {
        final Transaction trans = context.transaction();
        TraverseFieldCommand command = new TraverseFieldCommand() {
            private int fieldIndex = -1; 
            public int fieldCount(ClassMetadata classMetadata, ReadBuffer buffer) {
                int fieldCount = classMetadata.i_fields.length;
                context.fieldCount(fieldCount);
                return fieldCount;
            }
            public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                context.beginSlot();
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


    
}