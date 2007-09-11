/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class ObjectMarshaller1 extends ObjectMarshaller{

    public void addFieldIndices(final ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, final Slot oldSlot) {
		TraverseFieldCommand command = new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if (isNull) {
					field.addIndexEntry(writer.getTransaction(), writer.getID(), null);
				} 
				else {
					field.addFieldIndex(_family, yc, writer, oldSlot);
				}
			}
		};
		traverseFields(yc, writer, attributes, command);
	}
    
    public TreeInt collectFieldIDs(TreeInt tree, ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, final String name) {
        final TreeInt[] ret={tree};
		TraverseFieldCommand command = new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if(isNull) {
					return;
				}
		        if (name.equals(field.getName())) {
		            ret[0] = field.collectIDs(_family, ret[0], writer);
		        } 
		        else {
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
		        if(isNull){
		            field.removeIndexEntry(writer.getTransaction(), writer.getID(), null);
		        }else{
		            field.delete(_family, writer, isUpdate);
		        }
			}
		};
		traverseFields(yc, writer, attributes, command);
    }

    public boolean findOffset(ClassMetadata yc, ObjectHeaderAttributes attributes, final Buffer reader, final FieldMetadata field) {
        final boolean[] ret={false};
		TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata curField, boolean isNull, ClassMetadata containingClass) {
		        if (curField == field) {
		        	ret[0]=!isNull;
		        	cancel();
		        	return;
		        }
		        if(!isNull){
		            curField.incrementOffset(reader);
		        }
			}
		};
		traverseFields(yc, reader, attributes, command);
		return ret[0];
    }
    
    public void instantiateFields(ClassMetadata yc, ObjectHeaderAttributes attributes, final ObjectReference yapObject, final Object onObject, final StatefulBuffer writer) {
        TraverseFieldCommand command = new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if (isNull) {
					field.set(onObject, null);
					return;
				} 
				boolean ok = false;
				try {
					field.instantiate(_family, yapObject,onObject, writer);
					ok = true;
				} catch (CorruptionException e) {
					// FIXME: should it be ignored
				} finally {
					if(!ok) {
						cancel();
					}
				}
			}
		};
		traverseFields(yc, writer, attributes, command);
    }
    
    public ObjectHeaderAttributes readHeaderAttributes(Buffer reader) {
        return new ObjectHeaderAttributes(reader);
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
    
    public void readVirtualAttributes(final Transaction trans, ClassMetadata yc, final ObjectReference yo, ObjectHeaderAttributes attributes, final Buffer reader) {
		TraverseFieldCommand command = new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if (!isNull) {
					field.readVirtualAttribute(trans, reader, yo);
				}
			}
		};
		traverseFields(yc, reader, attributes, command);
	}

    protected boolean isNull(FieldListInfo fieldList,int fieldIndex) {
    	return fieldList.isNull(fieldIndex);
    }

	public void defragFields(ClassMetadata yc,ObjectHeader header, final BufferPair readers) {
        TraverseFieldCommand command = new TraverseFieldCommand() {
        	
        	public int fieldCount(ClassMetadata yapClass, Buffer reader) {
        		return readers.readInt();
        	}
        	
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if (!isNull) {
					field.defragField(_family,readers);
				} 
			}
		};
		traverseFields(yc, null, header._headerAttributes, command);
	}

	public void writeObjectClassID(Buffer reader, int id) {
		reader.writeInt(-id);
	}

	public void skipMarshallerInfo(Buffer reader) {
		reader.incrementOffset(1);
	}
	
    public final void marshallUpdate(Transaction trans, int updateDepth, ObjectReference ref, Object obj) {
        MarshallingContext context = new MarshallingContext(trans, ref, updateDepth, false);
        marshall(obj, context);
        Pointer4 pointer = context.allocateSlot();
        marshallUpdateWrite(trans, pointer, ref, obj, context.ToWriteBuffer(pointer));
    }

    
    public StatefulBuffer marshallNew(Transaction trans, ObjectReference ref, int updateDepth){
        MarshallingContext context = new MarshallingContext(trans, ref, updateDepth, true);
        marshall(ref.getObject(), context);
        Pointer4 pointer = context.allocateSlot();
        return context.ToWriteBuffer(pointer);
    }
    
    protected void marshall(final Object obj, final MarshallingContext context) {
        final Transaction trans = context.transaction();
        TraverseFieldCommand command = new TraverseFieldCommand() {
            private int fieldIndex = -1; 
            public int fieldCount(ClassMetadata classMetadata, Buffer buffer) {
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
	
}
