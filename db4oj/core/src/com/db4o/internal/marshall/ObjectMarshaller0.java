/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
class ObjectMarshaller0 extends ObjectMarshaller {
    
    public void addFieldIndices(final ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, final Slot oldSlot) {
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
	            field.addFieldIndex(_family, yc, writer, oldSlot);
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    }
    
    public void deleteMembers(ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, int type, final boolean isUpdate){
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
	            field.delete(_family, writer, isUpdate);
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    }
    
    public boolean findOffset(ClassMetadata yc, FieldListInfo fieldListInfo, final ByteArrayBuffer buffer, final FieldMetadata field) {
    	final boolean[] ret={false};
    	TraverseFieldCommand command=new TraverseFieldCommand() {
    		public void processField(FieldMetadata curField, boolean isNull, ClassMetadata containingClass) {
	            if (curField == field) {
	                ret[0]=true;
	                cancel();
	                return;
	            }
	            curField.incrementOffset(buffer);
			}
    	};
    	traverseFields(yc, buffer, fieldListInfo, command);
    	return ret[0];
    }
    
    protected final int headerLength(){
        return Const4.OBJECT_LENGTH + Const4.ID_LENGTH;
    }
    
    /**
     * @param yf
     * @param yo
     */
    protected int marshalledLength(FieldMetadata yf, ObjectReference yo){
        return 0;
    }

    public ObjectHeaderAttributes readHeaderAttributes(ByteArrayBuffer reader) {
        return null;
    }

    public Object readIndexEntry(ClassMetadata clazz, ObjectHeaderAttributes attributes, FieldMetadata field, StatefulBuffer reader) throws FieldIndexException {
        if(clazz == null){
            return null;
        }
        
        if(! findOffset(clazz, attributes, reader, field)){
            return null;
        }
        
        try {
			return field.readIndexEntry(_family, reader);
		} catch (CorruptionException exc) {
			throw new FieldIndexException(exc,field);
		}
    }
    
    protected boolean isNull(FieldListInfo fieldList,int fieldIndex) {
    	return false;
    }

	public void writeObjectClassID(ByteArrayBuffer reader, int id) {
		reader.writeInt(id);
	}

	public void skipMarshallerInfo(ByteArrayBuffer reader) {
	}
    

}
