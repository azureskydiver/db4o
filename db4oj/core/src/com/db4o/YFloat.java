/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class YFloat extends YInt {
    
    private static final Float i_primitive = new Float(0);
    private static final Class i_class = i_primitive.getClass();
    
	public int getID() {
		return 3;
	}

	public Class getJavaClass() {
		return i_class;
	}

	public Class getPrimitiveJavaClass() {
		return float.class;
	}

	Object primitiveNull() {
		return i_primitive;
	}

	Object read1(YapReader a_bytes) {
		int ret = readInt(a_bytes);
		if(! Deploy.csharp){
			if (ret == Integer.MAX_VALUE) {
				return null;
			}
		}
		return new Float(Float.intBitsToFloat(ret));
	}

	public void write(Object a_object, YapWriter a_bytes) {
		if (! Deploy.csharp && a_object == null) {
			writeInt(Integer.MAX_VALUE, a_bytes);
		} else {
			writeInt(
				Float.floatToIntBits(((Float) a_object).floatValue()),
				a_bytes);
		}
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

}
