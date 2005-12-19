package com.db4o.reflect.self;

import com.db4o.reflect.*;

public class SelfArray implements ReflectArray {
	private final Reflector _reflector;
	private final SelfReflectionRegistry _registry;

	SelfArray(Reflector reflector,SelfReflectionRegistry registry) {
		_reflector = reflector;
		_registry=registry;
	}

	public int[] dimensions(Object arr) {
		// TODO Auto-generated method stub
		return null;
	}

	public int flatten(Object a_shaped, int[] a_dimensions,
			int a_currentDimension, Object[] a_flat, int a_flatElement) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object get(Object onArray, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public ReflectClass getComponentType(ReflectClass a_class) {
		return ((SelfClass)a_class).getComponentType();
	}

	public int getLength(Object array) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isNDimensional(ReflectClass a_class) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object newInstance(ReflectClass componentType, int length) {
		return _registry.arrayFor(((SelfClass)componentType).getJavaClass(),length);
	}

	public Object newInstance(ReflectClass componentType, int[] dimensions) {
		// TODO Auto-generated method stub
		return null;
	}

	public void set(Object onArray, int index, Object element) {
		// TODO Auto-generated method stub

	}

	public int shape(Object[] a_flat, int a_flatElement, Object a_shaped,
			int[] a_dimensions, int a_currentDimension) {
		// TODO Auto-generated method stub
		return 0;
	}

}
