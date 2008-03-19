/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers.net;

import com.db4o.internal.handlers.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public abstract class NetSimpleTypeHandler extends NetTypeHandler implements GenericConverter{
	
	private final String _name;
	private final int _typeID;
	private final int _byteCount;
	
	public NetSimpleTypeHandler(Reflector reflector, int typeID, int byteCount) {
        super();
        _name = dotNetClassName();
        _typeID = typeID;
        _byteCount = byteCount;
        _classReflector = reflector.forName(_name);
    }
	
    public ReflectClass classReflector(){
    	return _classReflector;  
    }
	
	public Object defaultValue() {
		return new byte[_byteCount];
	}
	
	public String getName() {
		return _name;
	}
	
	public int typeID() {
		return _typeID;
	}
	
	public void write(Object obj, byte[] bytes, int offset) {
		byte[] objBytes = bytesFor(obj);
		System.arraycopy(objBytes, 0, bytes, offset, objBytes.length);
	}

	public Object read(byte[] bytes, int offset) {
		byte[] ret = new byte[_byteCount];
		System.arraycopy(bytes, offset, ret, 0, ret.length);
		GenericObject go = new GenericObject((GenericClass)classReflector());
		go.set(0, ret);
		return go;
	}
	
	GenericObject genericObject(Object obj) {
		if(obj != null) {
			return (GenericObject)obj;	
		}
		GenericObject go = new GenericObject((GenericClass)classReflector()); 
		go.set(0, defaultValue());
		return go;
	}
	
	byte[] genericObjectBytes(Object obj) {
		GenericObject go = genericObject(obj);
		return (byte[])go.get(0);
	}
	
	byte[] bytesFor(Object obj) {
		if(obj instanceof byte[]) {
			return (byte[])obj;
		}
		return genericObjectBytes(obj);
	}
	
	public String toString(GenericObject obj) {
		return toString((byte[])obj.get(0));
	}
	
    /** @param bytes */
	public String toString(byte[] bytes) {
		return ""; //$NON-NLS-1$
	}
	
	
}
