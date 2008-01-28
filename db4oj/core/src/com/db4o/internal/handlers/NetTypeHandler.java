/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.CorruptionException;
import com.db4o.foundation.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public abstract class NetTypeHandler extends PrimitiveHandler implements NetType{
    
	public NetTypeHandler(ObjectContainerBase stream) {
        super(stream);
    }

	private int i_linkLength;
	
    public String dotNetClassName(){
        String className = this.getClass().getName();
        int pos = className.indexOf(".Net") ;
        if(pos >=0){
            return "System." + className.substring(pos + 4) + ", mscorlib";    
        }
        return defaultValue().getClass().getName();
    }
	
	public void initialize(){
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
	
	public int getID() {
		return typeID();
	}
	
	// This method is needed for NetSimpleTypeHandler only during
    // initalisation and overloaded there. No abstract declaration 
    // here, so we don't have to implement the methods on .NET.
	public String getName() {
		return dotNetClassName();
	}

	public int linkLength() {
		return i_linkLength;
	}

    protected Class primitiveJavaClass(){
        return null;
    }
    
    public Object primitiveNull() {
        return defaultValue();
    }
    
    public abstract Object read(byte[] bytes, int offset);
    
    Object read1(ByteArrayBuffer a_bytes) throws CorruptionException {
		int offset = a_bytes._offset;
		Object ret = read(a_bytes._buffer, a_bytes._offset);
		a_bytes._offset = offset + linkLength();
		return ret;
	}
    
    public abstract int typeID();
    
    public abstract void write(Object obj, byte[] bytes, int offset);

    public void write(Object a_object, ByteArrayBuffer a_bytes) {
        int offset = a_bytes._offset;
        if(a_object != null){
            write(a_object, a_bytes._buffer, a_bytes._offset);
        }
        a_bytes._offset = offset + linkLength();
    }

	public PreparedComparison internalPrepareComparison(Object obj) {
		throw new NotImplementedException();
	}
}
