/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class YDouble extends YLong
{
    private static final Double i_primitive = new Double(0);
    private static final Class i_class = i_primitive.getClass();
    
	public int getID(){
		return 5;
	}
	
	public Class getJavaClass(){
		return i_class;
	}
	
	public Class getPrimitiveJavaClass(){
		return double.class;
	}
	
	Object primitiveNull(){
		return i_primitive;
	}
	
	Object read1(YapReader a_bytes){
		long ret = readLong(a_bytes);
		if(! Deploy.csharp){
			if(ret == Long.MAX_VALUE){
				return null;
			}
		}
		return new Double(Platform.longToDouble(ret));
	}
	
	public void write(Object a_object, YapWriter a_bytes){
		if (! Deploy.csharp && a_object == null){
			writeLong(Long.MAX_VALUE,a_bytes);
		} else {
			writeLong(Platform.doubleToLong(((Double)a_object).doubleValue()), a_bytes);
		}
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
	
	
}
