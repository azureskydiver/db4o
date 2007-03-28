/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.CorruptionException;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.MarshallerFamily;
import com.db4o.reflect.ReflectClass;



public final class FloatHandler extends IntHandler {
    
    private static final Float i_primitive = new Float(0);
    
    public FloatHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toFloat(obj);
    }

	public Object defaultValue(){
		return i_primitive;
	}
	
	public int getID() {
		return 3;
	}

	protected Class primitiveJavaClass() {
		return float.class;
	}

	public Object primitiveNull() {
		return i_primitive;
	}
	
	public Object read(MarshallerFamily mf, StatefulBuffer writer, boolean redirect) throws CorruptionException {
    	return mf._primitive.readFloat(writer);
    }

	Object read1(Buffer a_bytes) {
		return primitiveMarshaller().readFloat(a_bytes);
	}


	public void write(Object a_object, Buffer a_bytes) {
		writeInt(
			Float.floatToIntBits(((Float) a_object).floatValue()),
			a_bytes);
	}

	// Comparison_______________________

	private float i_compareTo;

	private float valu(Object obj) {
		return ((Float) obj).floatValue();
	}

	void prepareComparison1(Object obj) {
		i_compareTo = valu(obj);
	}
    
    public Object current1(){
        return new Float(i_compareTo);
    }

	boolean isEqual1(Object obj) {
		return obj instanceof Float && valu(obj) == i_compareTo;
	}

	boolean isGreater1(Object obj) {
		return obj instanceof Float && valu(obj) > i_compareTo;
	}

	boolean isSmaller1(Object obj) {
		return obj instanceof Float && valu(obj) < i_compareTo;
	}

}
