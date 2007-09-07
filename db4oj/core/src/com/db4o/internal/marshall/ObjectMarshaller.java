/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public abstract class ObjectMarshaller {
    
    public MarshallerFamily _family;
    
	protected abstract static class TraverseFieldCommand {
		private boolean _cancelled=false;
		
		public int fieldCount(ClassMetadata classMetadata, Buffer reader) {
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
	    traverseFields(context.classMetadata(), context.buffer(), context, command);
	}
	
    protected final void traverseFields(ClassMetadata classMetadata, Buffer buffer, FieldListInfo fieldList,TraverseFieldCommand command) {
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
    
    public abstract TreeInt collectFieldIDs(
        TreeInt tree, 
        ClassMetadata yc, 
        ObjectHeaderAttributes attributes, 
        StatefulBuffer reader, String name);
    
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
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            Buffer reader, 
            FieldMetadata field);
    
    public abstract void instantiateFields(
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            ObjectReference yo, 
            Object obj, 
            StatefulBuffer reader);
    
    public abstract StatefulBuffer marshallNew(Transaction a_trans, ObjectReference yo, int a_updateDepth);
    
    public abstract void marshallUpdate(
        Transaction a_trans,
        int a_updateDepth,
        ObjectReference a_yapObject,
        Object a_object
        );
    
    protected void marshallUpdateWrite(
            Transaction trans, 
            ObjectReference yo, 
            Object obj, 
            StatefulBuffer writer) {
        
        ClassMetadata yc = yo.classMetadata();
        
        ObjectContainerBase stream = trans.container();
        stream.writeUpdate(yc, writer);
        if (yo.isActive()) {
            yo.setStateClean();
        }
        yo.endProcessing();
        objectOnUpdate(trans, yc, obj);
    }

	private void objectOnUpdate(Transaction transaction, ClassMetadata yc, Object obj) {
		ObjectContainerBase container = transaction.container();
		container.callbacks().objectOnUpdate(transaction, obj);
		yc.dispatchEvent(container, obj, EventDispatcher.UPDATE);
	}
    
    public abstract Object readIndexEntry(
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            FieldMetadata yf, 
            StatefulBuffer reader);

    public abstract ObjectHeaderAttributes readHeaderAttributes(Buffer reader);
    
    public abstract void readVirtualAttributes(
            Transaction trans,  
            ClassMetadata yc, 
            ObjectReference yo, 
            ObjectHeaderAttributes attributes, 
            Buffer reader);

	public abstract void defragFields(ClassMetadata yapClass,ObjectHeader header, BufferPair readers);
 
	public abstract void writeObjectClassID(Buffer reader,int id);
	
	public abstract void skipMarshallerInfo(Buffer reader);

    public abstract void instantiateFields(UnmarshallingContext context);
    
}