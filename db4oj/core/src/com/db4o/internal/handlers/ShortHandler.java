/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.internal.*;
import com.db4o.reflect.ReflectClass;



public final class ShortHandler extends PrimitiveHandler
{
    static final int LENGTH = Const4.SHORT_BYTES + Const4.ADDED_LENGTH;
	
	private static final Short i_primitive = new Short((short)0);
	
    public ShortHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toShort(obj);
    }
    public Object defaultValue(){
		return i_primitive;
	}
	
	public int getID(){
		return 8;
	}
	
	public int linkLength(){
		return LENGTH;
	}
	
	protected Class primitiveJavaClass(){
		return short.class;
	}
	
	public Object primitiveNull(){
		return i_primitive;
	}
	
	Object read1(Buffer a_bytes){
		return new Short(readShort(a_bytes));
	}
	
	static final short readShort(Buffer a_bytes){
		int ret = 0;
		if (Deploy.debug){
			a_bytes.readBegin(Const4.YAPSHORT);
		}
		for (int i = 0; i < Const4.SHORT_BYTES; i++){
			ret = (ret << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
		}
		if (Deploy.debug){
			a_bytes.readEnd();
		}
		return (short)ret;
	}

	public void write(Object a_object, Buffer a_bytes){
	    writeShort(((Short)a_object).shortValue(), a_bytes);
	}
	
	static final void writeShort(int a_short, Buffer a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(Const4.YAPSHORT);
		}
		for (int i = 0; i < Const4.SHORT_BYTES; i++){
			a_bytes._buffer[a_bytes._offset++] = (byte) (a_short >> ((Const4.SHORT_BYTES - 1 - i) * 8));
		}
		if(Deploy.debug){
			a_bytes.writeEnd();
		}
	}
	
	// Comparison_______________________
	
	private short i_compareTo;
	
	private short val(Object obj){
		return ((Short)obj).shortValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
	}
    
    public Object current1(){
        return new Short(i_compareTo);
    }
	
	boolean isEqual1(Object obj){
		return obj instanceof Short && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Short && val(obj) > i_compareTo;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Short && val(obj) < i_compareTo;
	}
	
	
}
