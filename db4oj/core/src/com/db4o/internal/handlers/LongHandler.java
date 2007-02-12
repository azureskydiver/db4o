/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
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
	
	public static final long readLong(Buffer bytes){
		long ret = 0;
		if (Deploy.debug){
			bytes.readBegin(Const4.YAPLONG);
			if(Deploy.debugLong){
				ret = Long.parseLong(new LatinStringIO().read(bytes, Const4.LONG_BYTES).trim()); 
			}else{
				for (int i = 0; i < Const4.LONG_BYTES; i++){
					ret = (ret << 8) + (bytes._buffer[bytes._offset++] & 0xff);
				}
			}
			bytes.readEnd();
		}else{
			ret = PrimitiveCodec.readLong(bytes._buffer, bytes._offset);
			incrementOffset(bytes);
		}
		return ret;
	}

	public void write(Object obj, Buffer buffer){
	    writeLong(((Long)obj).longValue(), buffer);
	}
	
	public static final void writeLong(long val, Buffer bytes){
		if(Deploy.debug){
			bytes.writeBegin(Const4.YAPLONG);
			if(Deploy.debugLong){
				String l_s = "                                " + val;
				new LatinStringIO().write(bytes, l_s.substring(l_s.length() - Const4.LONG_BYTES));
			}
			else{
				for (int i = 0; i < Const4.LONG_BYTES; i++){
					bytes._buffer[bytes._offset++] = (byte) (val >> ((Const4.LONG_BYTES - 1 - i) * 8));
				}
			}
			bytes.writeEnd();
		}else{
			PrimitiveCodec.writeLong(bytes._buffer, bytes._offset,  val);
			incrementOffset(bytes);
		}
	}
	
	private static final void incrementOffset(Buffer buffer){
		buffer.incrementOffset(Const4.LONG_BYTES);
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
