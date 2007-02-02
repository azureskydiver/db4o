/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.internal.*;
import com.db4o.reflect.ReflectClass;



/**
 * @exclude
 */
public class LongHandler extends PrimitiveHandler {

    private static final Long i_primitive = new Long(0);

    public LongHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toLong(obj);
    }
    
    public Object defaultValue(){
		return i_primitive;
	}
	
	public int getID(){
		return 2;
	}
	
	protected Class primitiveJavaClass(){
		return long.class;
	}
	
	public int linkLength(){
		return Const4.LONG_LENGTH;
	}
	
	public Object primitiveNull(){
		return i_primitive;
	}
	
	Object read1(Buffer a_bytes){
		return new Long(readLong(a_bytes));
	}
	
	public static final long readLong(Buffer a_bytes){
		long l_return = 0;
		if (Deploy.debug){
			a_bytes.readBegin(Const4.YAPLONG);
			if(Deploy.debugLong){
				l_return = Long.parseLong(new LatinStringIO().read(a_bytes, Const4.LONG_BYTES).trim()); 
			}else{
				for (int i = 0; i < Const4.LONG_BYTES; i++){
					l_return = (l_return << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
				}
			}
			a_bytes.readEnd();
		}else{
			for (int i = 0; i < Const4.LONG_BYTES; i++){
				l_return = (l_return << 8) + (a_bytes._buffer[a_bytes._offset++] & 0xff);
			}
		}
		return l_return;
	}

	public void write(Object a_object, Buffer a_bytes){
	    writeLong(((Long)a_object).longValue(), a_bytes);
	}
	
	public static final void writeLong(long a_long, Buffer a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(Const4.YAPLONG);
			if(Deploy.debugLong){
				String l_s = "                                " + a_long;
				new LatinStringIO().write(a_bytes, l_s.substring(l_s.length() - Const4.LONG_BYTES));
			}
			else{
				for (int i = 0; i < Const4.LONG_BYTES; i++){
					a_bytes._buffer[a_bytes._offset++] = (byte) (a_long >> ((Const4.LONG_BYTES - 1 - i) * 8));
				}
			}
			a_bytes.writeEnd();
		}else{
			for (int i = 0; i < Const4.LONG_BYTES; i++){
				a_bytes._buffer[a_bytes._offset++] = (byte) (a_long >> ((Const4.LONG_BYTES - 1 - i) * 8));
			}
		}
	}	
	
	public static final void writeLong(long a_long, byte[] bytes){
		for (int i = 0; i < Const4.LONG_BYTES; i++){
			bytes[i] = (byte) (a_long >> ((Const4.LONG_BYTES - 1 - i) * 8));
		}
	}	
	
	
		
	// Comparison_______________________
	
	private long i_compareTo;
	
	protected final long currentLong() {
		return i_compareTo;
	}
	
	long val(Object obj){
		return ((Long)obj).longValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
	}
    
    public Object current1(){
        return new Long(i_compareTo);
    }
	
	boolean isEqual1(Object obj){
		return obj instanceof Long && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Long && val(obj) > i_compareTo;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Long && val(obj) < i_compareTo;
	}
	
}
