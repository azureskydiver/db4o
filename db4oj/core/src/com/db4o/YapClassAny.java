/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * Undefined YapClass used for members of type Object.
 */
final class YapClassAny extends YapClass {
    
	public YapClassAny(YapStream stream){
		super(stream, stream.i_handlers.ICLASS_OBJECT);
	}

	public boolean canHold(ReflectClass claxx) {
		return true;
	}

	public static void appendEmbedded(YapWriter a_bytes) {
        ObjectHeader oh = new ObjectHeader(a_bytes);
		if (oh._yapClass != null) {
            oh._yapClass.appendEmbedded1(a_bytes);
		}
	}

	public void cascadeActivation(
		Transaction a_trans,
		Object a_object,
		int a_depth,
		boolean a_activate) {
		YapClass yc = a_trans.i_stream.getYapClass(a_trans.reflector().forObject(a_object), false);
		if (yc != null) {
			yc.cascadeActivation(a_trans, a_object, a_depth, a_activate);
		}
	}

	public void deleteEmbedded(MarshallerFamily mf, YapWriter a_bytes) {
		int objectID = a_bytes.readInt();
		if (objectID > 0) {
			YapWriter reader =
				a_bytes.getStream().readWriterByID(a_bytes.getTransaction(), objectID);
			if (reader != null) {
				reader.setCascadeDeletes(a_bytes.cascadeDeletes());
                ObjectHeader oh = new ObjectHeader(reader);
				if(oh._yapClass != null){
                    
                    // FIXME: SM remove
                    mf = MarshallerFamily.forVersion(0);
                    
				    oh._yapClass.deleteEmbedded1(mf, reader, objectID);
				}
			}
		}
	}
	
	public int getID() {
		return 11;
	}

	public boolean hasField(YapStream a_stream, String a_path) {
		return a_stream.i_classCollection.fieldExists(a_path);
	}
	
	boolean hasIndex() {
	    return false;
	}

	public boolean holdsAnyClass() {
		return true;
	}
	
	boolean isStrongTyped(){
		return false;
	}

	public TypeHandler4 readArrayWrapper(Transaction a_trans, YapReader[] a_bytes) {

		int id = 0;

		int offset = a_bytes[0]._offset;
		try {
			id = a_bytes[0].readInt();
		} catch (Exception e) {
		}
		a_bytes[0]._offset = offset;

		if (id != 0) {
			YapWriter reader =
				a_trans.i_stream.readWriterByID(a_trans, id);
			if (reader != null) {
                ObjectHeader oh = new ObjectHeader(reader);
				try {
					if (oh._yapClass != null) {
						a_bytes[0] = reader;
						return oh._yapClass.readArrayWrapper1(a_bytes);
					}
				} catch (Exception e) {
                    
                    if(Debug.atHome){
                        e.printStackTrace();
                    }
                    
					// TODO: Check Exception Types
					// Errors typically occur, if classes don't match
				}
			}
		}
		return null;
	}
	
    public boolean supportsIndex() {
        return false;
    }


}
