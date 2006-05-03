/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * Common base class for YapString and YapArray:
 * There is one indirection in the database file to this.
 * 
 * @exclude
 */
public abstract class YapIndependantType implements TypeHandler4 {
    final YapStream _stream;
    
    public YapIndependantType(YapStream stream) {
        _stream = stream;
    }
    
    
    public Object coerce(ReflectClass claxx, Object obj) {
        return canHold(claxx) ? obj : No4.INSTANCE;
    }
	
	public final void copyValue(Object a_from, Object a_to){
		// do nothing
	}
    
    public abstract void deleteEmbedded(MarshallerFamily mf, YapWriter a_bytes);
    
    public final int linkLength(){
        return YapConst.YAPINT_LENGTH + YapConst.YAPID_LENGTH;
    }
    
	public final ReflectClass primitiveClassReflector(){
		return null;
	}
    
    public boolean readArray(Object array, YapWriter reader) {
        return false;
    }
	
    public Object readIndexEntry(MarshallerFamily mf, YapWriter a_writer) throws CorruptionException{
        return read(mf, a_writer);
    }
	
    public boolean writeArray(Object array, YapWriter reader) {
        return false;
    }
    
    // redundant, only added to make Sun JDK 1.2's java happy :(
    public abstract boolean isGreater(Object obj);	
    public abstract YapComparable prepareComparison(Object obj);
    public abstract int compareTo(Object obj);
    public abstract boolean isEqual(Object obj);
    public abstract boolean isSmaller(Object obj);
    
    public abstract Object comparableObject(Transaction trans, Object indexEntry);
    public abstract Object readIndexEntry(YapReader a_reader);
    public abstract void writeIndexEntry(YapReader a_writer, Object a_object);
}
