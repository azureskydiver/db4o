/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.handlers.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

/**
 * @exclude
 */
public abstract class NetSimpleTypeHandler extends YapTypeAbstract implements GenericConverter{
	
	private final String _name;
	private final int _typeID;
	private final int _byteCount;
	
	public NetSimpleTypeHandler(ObjectContainerBase stream, int typeID, int byteCount) {
        super(stream);
        _name = dotNetClassName();
        _typeID = typeID;
        _byteCount = byteCount;
    }
	
    public ReflectClass classReflector(){
        if(_classReflector != null){
        	return _classReflector;
        }
        _classReflector = _stream.reflector().forName(_name);
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
	
	public int compare(Object compare, Object with) {
		byte[] byteCompare = bytesFor(compare);
		byte[] byteWith = bytesFor(with);
		int min = byteCompare.length < byteWith.length ? byteCompare.length : byteWith.length;
        for(int i = 0;i < min;i++) {
            if (byteCompare[i] != byteWith[i]) {
                return byteWith[i] - byteCompare[i];
            }
            
        }
        return byteWith.length - byteCompare.length;
	}

	public boolean isEqual(Object cmp, Object with) {
		return compare(cmp, with) == 0;
	}
	
	public String toString(GenericObject obj) {
		return toString((byte[])obj.get(0));
	}
	
	public String toString(byte[] bytes) {
		return "";
	}
	
	
}
