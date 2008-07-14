/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

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
			
			// FIXME:  If ClassMetadata doesn't use the default _typeHandler
			//         we can't traverse it's fields. 
			
			//         We should stop processing ancestors if  
			//         ClassMetadata#defaultObjectHandlerIsUsed() returns false
			//         on the ancestor
			
			
			classMetadata=classMetadata.i_ancestor;
    	}
    }

    protected abstract boolean isNull(FieldListInfo fieldList,int fieldIndex);

    public abstract void addFieldIndices(
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            StatefulBuffer writer, 
            Slot oldSlot) ;
    
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
    
    public abstract Object readIndexEntry(
            ClassMetadata yc, 
            ObjectHeaderAttributes attributes, 
            FieldMetadata yf, 
            StatefulBuffer reader);

    public abstract ObjectHeaderAttributes readHeaderAttributes(ByteArrayBuffer reader);
    
	public abstract void writeObjectClassID(ByteArrayBuffer reader,int id);
	
	public abstract void skipMarshallerInfo(ByteArrayBuffer reader);



    
}