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
    
    private void marshall(final ObjectReference ref, final Object obj, ObjectHeaderAttributes1 attributes, final StatefulBuffer writer, final boolean isNew) {
		ClassMetadata classMetadata = ref.getYapClass();
		writeObjectClassID(writer,classMetadata.getID());
		attributes.write(writer);
		classMetadata.checkUpdateDepth(writer);
		final Transaction trans = writer.getTransaction();

		TraverseFieldCommand command = new TraverseFieldCommand() {
			public int fieldCount(ClassMetadata yapClass, Buffer reader) {
				reader.writeInt(yapClass.i_fields.length);
				return yapClass.i_fields.length;
			}
			
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if(isNull) {
					field.addIndexEntry(trans, writer.getID(), null);
					return;
				}
				Object child = field.getOrCreate(trans, obj);
				if (child instanceof Db4oTypeImpl) {
					child = ((Db4oTypeImpl) child).storedTo(trans);
				}
				field.marshall(ref, child, _family, writer, containingClass.configOrAncestorConfig(), isNew);
			}
		};
		traverseFields(classMetadata, writer, attributes, command);
		if (Deploy.debug) {
			writer.writeEnd();
			writer.debugCheckBytes();
		}
	}

    public StatefulBuffer marshallNew(Transaction a_trans, ObjectReference yo, int a_updateDepth){
        
        ObjectHeaderAttributes1 attributes = new ObjectHeaderAttributes1(yo);
        
        StatefulBuffer writer = createWriterForNew(
            a_trans, 
            yo, 
            a_updateDepth, 
            attributes.objectLength());
        
        marshall(yo, yo.getObject(), attributes, writer, true);
        
        return writer;
    }
    
    public void marshallUpdate(
        Transaction trans,
        int updateDepth,
        ObjectReference yo,
        Object obj
        ) {
        
        ObjectHeaderAttributes1 attributes = new ObjectHeaderAttributes1(yo);
        
        StatefulBuffer writer = createWriterForUpdate(
            trans, 
            updateDepth, 
            yo.getID(), 
            0, 
            attributes.objectLength());
        
        if(trans instanceof LocalTransaction){
            // Running in single mode or on server.
            // We need the slot now, so indexes can adjust to address.
            ((LocalTransaction)trans).file().getSlotForUpdate(writer);
        }
        
        marshall(yo, obj, attributes, writer, false);
        
        marshallUpdateWrite(trans, yo, obj, writer);
    }
    
    public ObjectHeaderAttributes readHeaderAttributes(Buffer reader) {
        return new ObjectHeaderAttributes1(reader);
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

    protected boolean isNull(ObjectHeaderAttributes attributes,int fieldIndex) {
    	return ((ObjectHeaderAttributes1)attributes).isNull(fieldIndex);
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
}
