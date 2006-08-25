/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

public abstract class ObjectMarshaller {
    
    public MarshallerFamily _family;
    
	protected abstract static class TraverseFieldCommand {
		private boolean _cancelled=false;
		
		public int fieldCount(YapClass yapClass,YapReader reader) {
			return (Debug.atHome ? yapClass.readFieldCountSodaAtHome(reader) : yapClass.readFieldCount(reader));
		}

		public boolean cancelled() {
			return _cancelled;
		}
		
		protected void cancel() {
			_cancelled=true;
		}

		public abstract void processField(YapField field,boolean isNull, YapClass containingClass);
	}

    protected void traverseFields(YapClass yc,YapReader reader,ObjectHeaderAttributes attributes,TraverseFieldCommand command) {
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
            YapClass yc, 
            ObjectHeaderAttributes attributes, 
            YapWriter writer, 
            boolean isNew) ;
    
    public abstract TreeInt collectFieldIDs(
        TreeInt tree, 
        YapClass yc, 
        ObjectHeaderAttributes attributes, 
        YapWriter reader, String name);
    
    protected YapWriter createWriterForNew(
            Transaction trans, 
            YapObject yo, 
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

    protected YapWriter createWriterForUpdate(
            Transaction a_trans, 
            int updateDepth, 
            int id, 
            int address, 
            int length) {
        
        YapWriter writer = new YapWriter(a_trans, length);
        writer.useSlot(id, address, length);
        if (Deploy.debug) {
            writer.writeBegin(YapConst.YAPOBJECT, length);
        }
        writer.setUpdateDepth(updateDepth);
        return writer;
    }
    
    public abstract void deleteMembers(
            YapClass yc, 
            ObjectHeaderAttributes attributes, 
            YapWriter writer, 
            int a_type, 
            boolean isUpdate);
    
    public abstract boolean findOffset(
            YapClass yc, 
            ObjectHeaderAttributes attributes, 
            YapReader reader, 
            YapField field);
    
    public abstract void instantiateFields(
            YapClass yc, 
            ObjectHeaderAttributes attributes, 
            YapObject yo, 
            Object obj, 
            YapWriter reader);
    
    public abstract YapWriter marshallNew(Transaction a_trans, YapObject yo, int a_updateDepth);
    
    public abstract void marshallUpdate(
        Transaction a_trans,
        int a_updateDepth,
        YapObject a_yapObject,
        Object a_object
        );
    
    protected void marshallUpdateWrite(
            Transaction trans, 
            YapObject yo, 
            Object obj, 
            YapWriter writer) {
        
        YapClass yc = yo.getYapClass();
        
        YapStream stream = trans.stream();
        stream.writeUpdate(yc, writer);
        if (yo.isActive()) {
            yo.setStateClean();
        }
        yo.endProcessing();
        objectOnUpdate(yc, stream, obj);
    }

	private void objectOnUpdate(YapClass yc, YapStream stream, Object obj) {
		stream.callbacks().objectOnUpdate(obj);
		yc.dispatchEvent(stream, obj, EventDispatcher.UPDATE);
	}
    
    public abstract Object readIndexEntry(
            YapClass yc, 
            ObjectHeaderAttributes attributes, 
            YapField yf, 
            YapWriter reader);

    public abstract ObjectHeaderAttributes readHeaderAttributes(YapReader reader);
    
    public abstract void readVirtualAttributes(
            Transaction trans,  
            YapClass yc, 
            YapObject yo, 
            ObjectHeaderAttributes attributes, 
            YapReader reader);

	public abstract void defragFields(YapClass yapClass,ObjectHeader header, YapReader source, YapReader target, IDMapping mapping);
 
	public abstract void writeObjectClassID(YapReader reader,int id);
	
	public abstract void skipMarshallerInfo(YapReader reader);
}