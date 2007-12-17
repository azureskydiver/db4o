/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.CorruptionException;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.MarshallerFamily;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteContext;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class DoubleHandler extends LongHandler {
	
    private static final Double DEFAULT_VALUE = new Double(0);
    
    public DoubleHandler(ObjectContainerBase stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toDouble(obj);
    }

	public Object defaultValue(){
		return DEFAULT_VALUE;
	}
	
	protected Class primitiveJavaClass(){
		return double.class;
	}
	
	public Object primitiveNull(){
		return DEFAULT_VALUE;
	}
	
	public Object read(MarshallerFamily mf, StatefulBuffer buffer,
			boolean redirect) throws CorruptionException {
		return mf._primitive.readDouble(buffer);
	}
	
	Object read1(BufferImpl buffer){
		return primitiveMarshaller().readDouble(buffer);
	}
	
	public void write(Object a_object, BufferImpl a_bytes){
		a_bytes.writeLong(Platform4.doubleToLong(((Double)a_object).doubleValue()));
	}
	
	
	// Comparison_______________________
	
	private double i_compareToDouble;
	
	private double dval(Object obj){
		return ((Double)obj).doubleValue();
	}
	
	void prepareComparison1(Object obj){
		i_compareToDouble = dval(obj);
	}
    
	boolean isEqual1(Object obj){
		return obj instanceof Double && dval(obj) == i_compareToDouble;
	}
	
	boolean isGreater1(Object obj){
		return obj instanceof Double && dval(obj) > i_compareToDouble;
	}
	
	boolean isSmaller1(Object obj){
		return obj instanceof Double && dval(obj) < i_compareToDouble;
	}

    public Object read(ReadContext context) {
        Long l = (Long)super.read(context);
        return new Double(Platform4.longToDouble(l.longValue()));
    }

    public void write(WriteContext context, Object obj) {
        context.writeLong(Platform4.doubleToLong(((Double)obj).doubleValue()));
    }
    
    public PreparedComparison internalPrepareComparison(Object source) {
    	final double sourceDouble = ((Double)source).doubleValue();
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				double targetDouble = ((Double)target).doubleValue();
				return sourceDouble == targetDouble ? 0 : (sourceDouble < targetDouble ? - 1 : 1); 
			}
		};
    }

    
    
}
