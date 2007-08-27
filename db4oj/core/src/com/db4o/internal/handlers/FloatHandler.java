/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.CorruptionException;
import com.db4o.Debug;
import com.db4o.Deploy;
import com.db4o.foundation.Coercion4;
import com.db4o.internal.Buffer;
import com.db4o.internal.Const4;
import com.db4o.internal.ObjectContainerBase;
import com.db4o.internal.StatefulBuffer;
import com.db4o.internal.marshall.MarshallerFamily;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;
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
    
	boolean isEqual1(Object obj) {
		return obj instanceof Float && valu(obj) == i_compareTo;
	}

	boolean isGreater1(Object obj) {
		return obj instanceof Float && valu(obj) > i_compareTo;
	}

	boolean isSmaller1(Object obj) {
		return obj instanceof Float && valu(obj) < i_compareTo;
	}

    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPFLOAT);
        }
        
        float f = Float.intBitsToFloat(context.readInt());
        
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        return new Float(f);
    }

    public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug.writeBegin(context, Const4.YAPFLOAT);
        }
        
        float f = ((Float)obj).floatValue();
        context.writeInt(Float.floatToIntBits(f));
        
        if (Deploy.debug) {
            Debug.writeEnd(context);
        }
    }
}
