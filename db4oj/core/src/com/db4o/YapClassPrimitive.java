/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


class YapClassPrimitive extends YapClass{
    
    final YapDataType i_handler;

    YapClassPrimitive(YapStream a_stream, YapDataType a_handler) {
        i_fields = YapField.EMPTY_ARRAY;
        i_handler = a_handler;
        i_objectLength = memberLength();
    }

    void activateFields(Transaction a_trans, Object a_object, int a_depth) {
        // Override
        // do nothing
    }


    final void addToIndex(YapFile a_stream, Transaction a_trans, int a_id) {
        // Override
        // Primitive Indices will be created later.
    }

    boolean allowsQueries() {
        return false;
    }

    public void appendEmbedded1(YapWriter a_bytes) {
        // do nothing
    }

    void cacheDirty(Collection4 col) {
        // do nothing
    }

    public boolean canHold(Class a_class) {
        // Do we need this at all???
        // Check if this method is ever called
        return i_handler.canHold(a_class);
    }

    void deleteEmbedded1(YapWriter a_bytes, int a_id) {
        
        if(i_handler instanceof YapArray){
            YapArray ya = (YapArray)i_handler;
            
            // TODO: the following checks, whether the array stores
            // primitives. There is one case that is not covered here:
            // If a primitive array is stored to an untyped array or
            // to an Object variable, they would need to be deleted 
            // and freed also. However, if they are untyped, every 
            // single one would have to be read an checked and this
            // would be extremely slow.
            
            // Solution: Store information, whether an object is 
            // primitive in our pointers, in the highest bit of the
            // length int.
            
            if(ya.i_isPrimitive){
                ya.deletePrimitiveEmbedded(a_bytes, this);
                a_bytes.getTransaction().freeOnCommit(
                    a_bytes.getID(),
                    a_bytes.getAddress(),
                    a_bytes.getLength());
                a_bytes.getTransaction().setPointer(a_bytes.getID(), 0, 0);
                return;
            }
        }
        
       if(i_handler instanceof YapClassAny){
            // Any-In-Any: Ignore delete 
            a_bytes.incrementOffset(i_handler.linkLength());
        }else{
            i_handler.deleteEmbedded(a_bytes);
        }
		
		// TODO: Was this freeing call necessary? 
		//   free(a_bytes.getTransaction(), a_id, a_bytes.getAddress(), a_bytes.getLength());
		
		free(a_bytes, a_id);
			
    }

    void deleteMembers(YapWriter a_bytes, int a_type) {
        if (a_type == YapConst.TYPE_ARRAY) {
            new YapArray(this, true).deletePrimitiveEmbedded(a_bytes, this);
        } else if (a_type == YapConst.TYPE_NARRAY) {
            new YapArrayN(this, true).deletePrimitiveEmbedded(a_bytes, this);
        }
    }
    
	final void free(Transaction a_trans, int a_id, int a_address, int a_length) {
		a_trans.freeOnCommit(a_address, a_address, a_length);
		a_trans.freePointer(a_id);
	}
	
	final void free(YapWriter a_bytes, int a_id) {
		  Transaction ta = a_bytes.getTransaction();
		  ta.freeOnCommit(a_bytes.getAddress(), a_bytes.getAddress(), a_bytes.getLength());
		  ta.freePointer(a_id);
	  }

    
	final ClassIndex getIndex() {
		return null;
	}
	
	public Class getJavaClass() {
	    return i_handler.getJavaClass();
	}
	
	boolean hasIndex() {
	    return false;
	}

    Object instantiate(YapObject a_yapObject, Object a_object, YapWriter a_bytes, boolean a_addToIDTree) {
        if (a_object == null) {
            try {
                a_object = i_handler.read(a_bytes);
            } catch (CorruptionException ce) {
                return null;
            }
            a_yapObject.setObjectWeak(a_bytes.getStream(), a_object);
        }
        a_yapObject.setStateClean();
        return a_object;
    }
    
    Object instantiateTransient(YapObject a_yapObject, Object a_object, YapWriter a_bytes) {
        try {
            return i_handler.read(a_bytes);
        } catch (CorruptionException ce) {
            return null;
        }
    }

    void instantiateFields(YapObject a_yapObject, Object a_onObject, YapWriter a_bytes) {
        Object obj = null;
        try {
            obj = i_handler.read(a_bytes);
        } catch (CorruptionException ce) {
            obj = null;
        }
        if (obj != null) {
            i_handler.copyValue(obj, a_onObject);
        }
    }

    public boolean isArray() {
        return i_id == YapHandlers.ANYARRAYID || i_id == YapHandlers.ANYARRAYNID;
    }
    
	boolean isStrongTyped(){
		return false;
	}

    void marshall(YapObject a_yapObject, Object a_object, YapWriter a_bytes, boolean a_new) {
        i_handler.writeNew(a_object, a_bytes);
    }

    void marshallNew(YapObject a_yapObject, YapWriter a_bytes, Object a_object) {
        i_handler.writeNew(a_object, a_bytes);
    }

    int memberLength() {
        return i_handler.linkLength() + YapConst.OBJECT_LENGTH + YapConst.YAPID_LENGTH;
    }

    public YapComparable prepareComparison(Object a_constraint) {
        i_handler.prepareComparison(a_constraint);
        return i_handler;
    }

    public YapDataType readArrayWrapper(Transaction a_trans, YapReader[] a_bytes) {
        if (isArray()) {
            return i_handler;
        }
        return null;
    }

    void removeFromIndex(Transaction ta, int id) {
        // do nothing
    }
    
    public boolean supportsIndex() {
        return false;
    }

    final boolean writeObjectBegin() {
        return false;
    }

}
