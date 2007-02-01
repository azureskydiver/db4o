/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.slots.*;

public abstract class ObjectMarshaller {
    
    public MarshallerFamily _family;
    
	protected abstract static class TraverseFieldCommand {
		private boolean _cancelled=false;
		
		public int fieldCount(ClassMetadata yapClass,Buffer reader) {
			return (Debug.atHome ? yapClass.readFieldCountSodaAtHome(reader) : yapClass.readFieldCount(reader));
		}

		public boolean cancelled() {
			return _cancelled;
		}
		
		protected void cancel() {
			_cancelled=true;
		}

		public abstract void processField(YapField field,boolean isNull, ClassMetadata containingClass);
	}

    protected void traverseFields(ClassMetadata yc,Buffer reader,ObjectHeaderAttributes attributes,TraverseFieldCommand command) {
    	int fieldIndex=0;
    	while(yc!=null&&!command.cancelled()) {
        	int fieldCount=command.fieldCount(yc, reader);
			for (int i = 0; i < fieldCount && !command.cancelled(); i++) {
				command.processField(yc.i_fields[i],isNull(attributes,fieldIndex),yc);
			    fieldIndex ++;
			}
			yc=yc.i_ancestor;
    	}
    }

    protected abstract boolean isNull(ObjectHeaderAttributes attributes,int fieldIndex);

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
        int address = -1;
        
        if(! trans.stream().isClient()){
            address = trans.i_file.getSlot(length); 
        }
        trans.setPointer(id, address, length);
        return createWriterForUpdate(trans, updateDepth, id, address, length);
    }

    protected StatefulBuffer createWriterForUpdate(
            Transaction a_trans, 
            int updateDepth, 
            int id, 
            int address, 
            int length) {
        
        StatefulBuffer writer = new StatefulBuffer(a_trans, length);
        writer.useSlot(id, address, length);
        if (Deploy.debug) {
            writer.writeBegin(YapConst.YAPOBJECT);
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
            YapField field);
    
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
        
        ClassMetadata yc = yo.getYapClass();
        
        ObjectContainerBase stream = trans.stream();
        stream.writeUpdate(yc, writer);
        if (yo.isActive()) {
            yo.setStateClean();
        }
        yo.endProcessing();
        objectOnUpdate(yc, stream, obj);
    }

	private void objectOnUpdate(ClassMetadata yc, ObjectContainerBase stream, Object obj) {
		stream.callbacks().objectOnUpdate(obj);
		yc.dispatchEvent(stream, obj, EventDispatcher.UPDATE);
	}
    
    public abstract Object readIndexEntry(
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            YapField yf, 
            StatefulBuffer reader);

    public abstract ObjectHeaderAttributes readHeaderAttributes(Buffer reader);
    
    public abstract void readVirtualAttributes(
            Transaction trans,  
            ClassMetadata yc, 
            ObjectReference yo, 
            ObjectHeaderAttributes attributes, 
            Buffer reader);

	public abstract void defragFields(ClassMetadata yapClass,ObjectHeader header, ReaderPair readers);
 
	public abstract void writeObjectClassID(Buffer reader,int id);
	
	public abstract void skipMarshallerInfo(Buffer reader);
}