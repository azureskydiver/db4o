/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.MarshallerFamily;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;
import com.db4o.reflect.ReflectClass;

public class ShortHandler extends PrimitiveHandler {
	
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
	
	public int linkLength(){
		return LENGTH;
	}
	
	protected Class primitiveJavaClass(){
		return short.class;
	}
	
	public Object primitiveNull(){
		return i_primitive;
	}
	
	public Object read(MarshallerFamily mf, StatefulBuffer buffer,
			boolean redirect) throws CorruptionException {

		return mf._primitive.readShort(buffer);
	}
	
	Object read1(BufferImpl buffer){
		return primitiveMarshaller().readShort(buffer);
	}

	public void write(Object a_object, BufferImpl a_bytes){
	    writeShort(((Short)a_object).shortValue(), a_bytes);
	}
	
	static final void writeShort(int a_short, BufferImpl a_bytes){
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
    
	boolean isEqual1(Object obj){
		return obj instanceof Short && val(obj) == i_compareTo;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Short && val(obj) > i_compareTo;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Short && val(obj) < i_compareTo;
	}

    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPSHORT);
        }
        int value = ((context.readByte() & 0xff) << 8) + (context.readByte() & 0xff);
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        return new Short((short) value);
    }

    public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPSHORT);
        }
        int shortValue = ((Short)obj).shortValue();
        context.writeBytes(new byte [] {
            (byte) (shortValue >> 8),
            (byte) shortValue
        });
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }
    
    public PreparedComparison internalPrepareComparison(Object source) {
    	final short sourceShort = ((Short)source).shortValue();
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				if(target == null){
					return 1;
				}
				short targetShort = ((Short)target).shortValue();
				return sourceShort == targetShort ? 0 : (sourceShort < targetShort ? - 1 : 1); 
			}
		};
    }
	

    
	
}
