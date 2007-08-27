/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.MarshallerFamily;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;
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
	
	public Object read(MarshallerFamily mf, StatefulBuffer buffer,
			boolean redirect) throws CorruptionException {
		return mf._primitive.readLong(buffer);
	}
	
	Object read1(Buffer a_bytes){
		return new Long(a_bytes.readLong());
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
    
	boolean isEqual1(Object obj){
		return obj instanceof Long && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Long && val(obj) > i_compareTo;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Long && val(obj) < i_compareTo;
	}

    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPLONG);
        }
        
        long longValue = 0;
        for (int i = 0; i < Const4.LONG_BYTES; i++) {
            byte b = context.readByte();
            longValue = (longValue << 8) + (b & 0xff);
        }
        
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        
        return new Long(longValue);
    }

    public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPLONG);
        }

        long longValue = ((Long) obj).longValue();
        for (int i = 0; i < Const4.LONG_BYTES; i++) {
            byte b = (byte) (longValue >> ((Const4.LONG_BYTES - 1 - i) * 8));
            context.writeByte(b);
        }
        
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }
}
