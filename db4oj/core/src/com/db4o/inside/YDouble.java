/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public final class YDouble extends YLong
{
    private static final Double i_primitive = new Double(0);
    
    public YDouble(YapStream stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toDouble(obj);
    }

	public Object defaultValue(){
		return i_primitive;
	}
	
	public int getID(){
		return 5;
	}
	
	protected Class primitiveJavaClass(){
		return double.class;
	}
	
	Object primitiveNull(){
		return i_primitive;
	}
	
	Object read1(Buffer a_bytes){
		return new Double(Platform4.longToDouble(readLong(a_bytes)));
	}
	
	public void write(Object a_object, Buffer a_bytes){
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
    
    public Object current1(){
        return new Double(i_compareToDouble);
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
	
	
}
