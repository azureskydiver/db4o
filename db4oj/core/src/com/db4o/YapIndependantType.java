/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.marshall.*;
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
    
    public boolean hasFixedLength(){
        return false;
    }
    
    public final int linkLength(){
        
        // TODO:  Now that array and string are embedded into their parent
        //        object from marshaller family 1 on, the length part is no
        //        longer needed. To stay compatible with marshaller family 0
        //        it was considered a bad idea to change this value.
        
        return YapConst.INT_LENGTH + YapConst.ID_LENGTH;
    }
    
	public final ReflectClass primitiveClassReflector(){
		return null;
	}
    
    public boolean readArray(Object array, YapReader reader) {
        return false;
    }
	
    public Object readIndexEntry(MarshallerFamily mf, YapWriter a_writer) throws CorruptionException{
        return read(mf, a_writer, true);
    }
	
    public boolean writeArray(Object array, YapReader reader) {
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
    
    public final void defrag(MarshallerFamily mf, ReaderPair readers) {
    	int linkLength = linkLength();
    	readers.incrementOffset(linkLength);
    }
}
