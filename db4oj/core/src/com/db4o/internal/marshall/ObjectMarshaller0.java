/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.foundation.*;
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
    
    public TreeInt collectFieldIDs(TreeInt tree, ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, final String name) {
    	final TreeInt[] ret={tree};
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
	            if (name.equals(field.getName())) {
	                ret[0] = field.collectIDs(_family, ret[0], writer);
	            } else {
	                field.incrementOffset(writer);
	            }
			}
    	};
    	traverseFields(yc, writer, attributes, command);
        return ret[0];
    }
    
    public void deleteMembers(ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, int type, final boolean isUpdate){
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
	            field.delete(_family, writer, isUpdate);
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    }
    
    public boolean findOffset(ClassMetadata yc, ObjectHeaderAttributes attributes, final Buffer writer, final FieldMetadata field) {
    	final boolean[] ret={false};
    	TraverseFieldCommand command=new TraverseFieldCommand() {
    		public void processField(FieldMetadata curField, boolean isNull, ClassMetadata containingClass) {
	            if (curField == field) {
	                ret[0]=true;
	                cancel();
	                return;
	            }
	            writer.incrementOffset(curField.linkLength());
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    	return ret[0];
    }
    
    protected final int headerLength(){
        return Const4.OBJECT_LENGTH + Const4.ID_LENGTH;
    }
    
    public void instantiateFields(ClassMetadata yc, ObjectHeaderAttributes attributes, final ObjectReference ref, final Object onObject, final StatefulBuffer writer) {
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				boolean ok = false;
                try {
					field.instantiate(_family, ref, onObject, writer);
					ok = true;
				} catch (CorruptionException e) {
					// FIXME: CorruptionException SHOULD BE IGNORED?
				} finally {
					if (!ok) {
						cancel();
					}
				}
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    }
    
    /** @param ref */
    protected int linkLength(FieldMetadata yf, ObjectReference ref){
        return yf.linkLength();
    }
    
    
    /**
     * @param yf
     * @param yo
     */
    protected int marshalledLength(FieldMetadata yf, ObjectReference yo){
        return 0;
    }

    public StatefulBuffer marshallNew(Transaction a_trans, ObjectReference yo, int a_updateDepth){
        throw new NotSupportedException();
    }

    public void marshallUpdate(
        Transaction trans,
        int updateDepth,
        ObjectReference yapObject,
        Object obj
        ) {
        
        throw new NotSupportedException();
        
    }

    public ObjectHeaderAttributes readHeaderAttributes(Buffer reader) {
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
    
    public void readVirtualAttributes(final Transaction trans,  ClassMetadata yc, final ObjectReference yo, ObjectHeaderAttributes attributes, final Buffer reader){
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
	            field.readVirtualAttribute(trans, reader, yo);
			}
    	};
    	traverseFields(yc, reader, attributes, command);
    }

    protected boolean isNull(FieldListInfo fieldList,int fieldIndex) {
    	return false;
    }

	public void defragFields(ClassMetadata yapClass,ObjectHeader header, BufferPair readers) {
	}

	public void writeObjectClassID(Buffer reader, int id) {
		reader.writeInt(id);
	}

	public void skipMarshallerInfo(Buffer reader) {
	}

    public void instantiateFields(UnmarshallingContext context) {
        instantiateFields(context.classMetadata(), context.headerAttributes(), context.reference(), context.persistentObject(), context.statefulBuffer());
    }
}
