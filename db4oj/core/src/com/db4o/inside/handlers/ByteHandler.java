/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.reflect.*;



public final class ByteHandler extends PrimitiveHandler
{

    static final int LENGTH = 1 + Const4.ADDED_LENGTH;
	
	private static final Byte i_primitive = new Byte((byte)0);
	
    public ByteHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toSByte(obj);
    }

	public int getID(){
		return 6;
	}
	
	public Object defaultValue(){
		return i_primitive;
	}
	
	public int linkLength(){
		return LENGTH;
	}

	protected Class primitiveJavaClass(){
		return byte.class;
	}
	
	public Object primitiveNull(){
		return i_primitive;
	}
	
	Object read1(Buffer a_bytes){
		if (Deploy.debug){
			a_bytes.readBegin(Const4.YAPBYTE);
		}
		byte ret = a_bytes.readByte();
		if (Deploy.debug){
			a_bytes.readEnd();
		}
		return new Byte(ret);
	}
	
	public void write(Object a_object, Buffer a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(Const4.YAPBYTE);
		}
		a_bytes.append(((Byte)a_object).byteValue());
		if(Deploy.debug){
			a_bytes.writeEnd();
		}
	}
	
	public boolean readArray(Object array, Buffer reader) {
        if(array instanceof byte[]){
            reader.readBytes((byte[])array);
            return true;
        }
        
        return false;
	}

    public boolean writeArray(Object array, Buffer writer) {
        if(array instanceof byte[]){
            writer.append((byte[])array);
            return true;
        }
        return false;
    }   
    

					
	// Comparison_______________________
	
	private byte i_compareTo;
	
	private byte val(Object obj){
		return ((Byte)obj).byteValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
	}
    
    public Object current1(){
        return new Byte(i_compareTo);
    }
	
	boolean isEqual1(Object obj){
		return obj instanceof Byte && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Byte && val(obj) > i_compareTo;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Byte && val(obj) < i_compareTo;
	}
}
