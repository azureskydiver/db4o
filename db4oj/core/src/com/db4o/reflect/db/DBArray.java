package com.db4o.reflect.db;

import com.db4o.reflect.*;

public class DBArray implements ReflectArray {
	public int[] dimensions(Object arr) {
		return null;
	}

	public int flatten(Object a_shaped, int[] a_dimensions,
			int a_currentDimension, Object[] a_flat, int a_flatElement) {
		return 0;
	}

	public Object get(Object onArray, int index) {
		return null;
	}

	public ReflectClass getComponentType(ReflectClass a_class) {
		return a_class;
	}

	public int getLength(Object array) {
		return 0;
	}

	public boolean isNDimensional(ReflectClass a_class) {
		return false;
	}

	public Object newInstance(ReflectClass componentType, int length) {
		return null;
	}

	public Object newInstance(ReflectClass componentType, int[] dimensions) {
		return null;
	}

	public void set(Object onArray, int index, Object element) {
	}

	public int shape(Object[] a_flat, int a_flatElement, Object a_shaped,
			int[] a_dimensions, int a_currentDimension) {
		return 0;
	}

}
