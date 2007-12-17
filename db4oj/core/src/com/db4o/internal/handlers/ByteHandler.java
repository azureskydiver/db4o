/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;
import com.db4o.reflect.*;

public final class ByteHandler extends PrimitiveHandler {

    static final int LENGTH = 1 + Const4.ADDED_LENGTH;
	
	private static final Byte DEFAULT_VALUE = new Byte((byte)0);
	
    public ByteHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toSByte(obj);
    }

	public Object defaultValue(){
		return DEFAULT_VALUE;
	}
	
	public int linkLength(){
		return LENGTH;
	}

	protected Class primitiveJavaClass(){
		return byte.class;
	}
	
	public Object primitiveNull(){
		return DEFAULT_VALUE;
	}
	
	Object read1(BufferImpl a_bytes){
		if (Deploy.debug){
			a_bytes.readBegin(Const4.YAPBYTE);
		}
		byte ret = a_bytes.readByte();
		if (Deploy.debug){
			a_bytes.readEnd();
		}
		return new Byte(ret);
	}
	
	public void write(Object a_object, BufferImpl a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(Const4.YAPBYTE);
		}
		a_bytes.writeByte(((Byte)a_object).byteValue());
		if(Deploy.debug){
			a_bytes.writeEnd();
		}
	}
					
	// Comparison_______________________
	
	private byte i_compareTo;
	
	private byte val(Object obj){
		return ((Byte)obj).byteValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareTo = val(obj);
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

    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPBYTE);
        }
        
        byte byteValue = context.readByte();
        
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        
        return new Byte(byteValue);
    }

    public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPBYTE);
        }
        
        context.writeByte(((Byte)obj).byteValue());
        
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }
    
    public PreparedComparison internalPrepareComparison(Object source) {
    	final byte sourceByte = ((Byte)source).byteValue();
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				byte targetByte = ((Byte)target).byteValue();
				return sourceByte == targetByte ? 0 : (sourceByte < targetByte ? - 1 : 1); 
			}
		};
    }

}
