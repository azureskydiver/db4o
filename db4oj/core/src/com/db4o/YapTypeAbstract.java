/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


abstract class YapTypeAbstract extends YapJavaClass implements YapType{
    
	public YapTypeAbstract(YapStream stream) {
        super(stream);
    }

	private int i_linkLength;
	
	private Object i_compareTo;
	
	public abstract int typeID();
	
	public abstract void write(Object obj, byte[] bytes, int offset);
	
	public abstract Object read(byte[] bytes, int offset);
	
	public abstract int compare(Object compare, Object with);
	
	public abstract boolean isEqual(Object compare, Object with);

	void initialize(){
		byte[] bytes = new byte[65];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = 55;  // TODO: Why 55? This is a '7'. Remove.
        }
        write(primitiveNull(), bytes, 0);
		for (int i = 0; i < bytes.length; i++) {
			if(bytes[i] == 55){
				i_linkLength = i;
				break;
			}
		}
	}
	
	Object primitiveNull() {
		return defaultValue();
	}

	public void write(Object a_object, YapWriter a_bytes) {
		int offset = a_bytes._offset;
		if(a_object != null){
			write(a_object, a_bytes._buffer, a_bytes._offset);
		}
		a_bytes._offset = offset + linkLength();
	}

	public int getID() {
		return typeID();
	}

	public int linkLength() {
		return i_linkLength;
	}

    protected Class primitiveJavaClass(){
        return null;
    }
    
    Object read1(YapReader a_bytes) throws CorruptionException {
		int offset = a_bytes._offset;
		Object ret = read(a_bytes._buffer, a_bytes._offset);
		a_bytes._offset = offset + linkLength();
		return ret;
	}

	void prepareComparison1(Object obj) {
		i_compareTo = obj;
	}

	boolean isEqual1(Object obj) {
		return isEqual(i_compareTo, obj);
	}

	boolean isGreater1(Object obj) {
		if(classReflector().isInstance(obj) && ! isEqual(i_compareTo, obj)){
			return compare(i_compareTo, obj) > 0;
		}
		return false;
	}

	boolean isSmaller1(Object obj) {
		if(classReflector().isInstance(obj)  && ! isEqual(i_compareTo, obj)){
			return compare(i_compareTo, obj) < 0;
		}
		return false;
	}
}
