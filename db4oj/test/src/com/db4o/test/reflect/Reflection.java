/* Copyright (C) 2004, 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.reflect;

import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;

/**
 * test for custom reflection implementations.
 * <br><br>
 * db4o internally uses java.lang.reflect.* by default. On platforms that
 * do not support this package, customized implementations may be written
 * to supply all the functionality of the interfaces in the com.db4o.reflect
 * package. The sources in this sample packages demonstrate, how db4o
 * accesses the java.lang.reflect.* functionality.
 * <br><br>
 * This TestReflect method may be used to test, if you own implementation
 * provides the functionality that db4o needs. You may call the test from
 * the command line by specifying the classname of your own class that
 * implements IReflect. Alternatively you can call the test(IReflect) method.
 */
public class Reflection extends Test {

	private final IReflect _reflector;
	private final IClass _classReflector;

    public Reflection() throws ClassNotFoundException {
        _reflector = new CReflect(Thread.currentThread().getContextClassLoader());
        _classReflector = _reflector.forName(TestReflectClass.class.getName());
	}

	public void testIClass() throws ClassNotFoundException {
		IField[] fields = _classReflector.getDeclaredFields();
		_assert(
			fields.length == TestReflectClass.FIELD_COUNT, "getDeclaredFields method failed.");
		for (int i = 0; i < fields.length; i++) {
			_assert(fields != null, "getDeclaredFields[" + i + "] is valid");
			String fieldName = fields[i].getName();
			IField fieldReflector = _classReflector.getDeclaredField(fieldName);
			_assert(
				fieldReflector != null,
				"getDeclaredField('" + fieldName + "') is valid");
		}

		tstIField();

		IClass abstractReflector =
			_reflector.forName(TestReflectAbstractClass.class.getName());
		_assert(abstractReflector.isAbstract(), "isAbstract");
		IClass interfaceReflector =
			_reflector.forName(TestReflectInterface.class.getName());
		_assert(interfaceReflector.isInterface(), "isInterface");
		Object instance = _classReflector.newInstance();
		_assert(instance != null, "newInstance");

	}

	private void tstIField() {
		tstIField1("myString", "HiBabe", String.class);
		tstIField1("myInt", new Integer(10), int.class);
		tstIField1("myTyped", new TestReflectClass(), TestReflectClass.class);
		tstIField1("myUntyped", "Foooo", Object.class);
		tstIField1("myUntyped", new TestReflectClass(), Object.class);
		_assert(
			_classReflector.getDeclaredField("myStatic").isStatic(),
			"IField.isStatic()");
		_assert(
			_classReflector.getDeclaredField("myTransient").isTransient(),
			"IField.isTransient()");
	}

	private void tstIField1(String fieldName, Object obj, Class clazz) {
        IClass claxx = _reflector.forClass(clazz);
		String fieldMessage =
			TestReflectClass.class.getName() + ":" + fieldName;
		TestReflectClass onObject = new TestReflectClass();
		IField fieldReflector = _classReflector.getDeclaredField(fieldName);
		fieldReflector.set(onObject, obj);
		Object got = fieldReflector.get(onObject);
		_assert(got != null, fieldMessage + " IField.get returns NULL");
		_assert(
			obj.equals(got),
			fieldMessage + " IField.get returns strange Object");
		_assert(
			fieldReflector.getName().equals(fieldName),
			"IField.getName()");
		_assert(fieldReflector.isPublic(), "IField.isPublic()");
		_assert(!fieldReflector.isStatic(), "IField.isStatic()");
		_assert(!fieldReflector.isTransient(), "IField.isTransient()");
		_assert(fieldReflector.getType().equals(claxx), "IField.getType()");
	}

	public void testIArray() {
		tstIArray1(new Object[] {"", "hi", "Cool"});
		tstIArray1(new Object[] {new Object(), new TestReflectClass(), "Woooa", new Integer(3)});
		tstIArray1(new Object[] {new TestReflectClass(),new TestReflectClass()});
		tstIArray2(new int[] {1,2,3});
		tstIArray2(new long[] {1L,2L,3L});
	}
	
	private void tstIArray1(Object[] elements){
		IArray array = _reflector.array();
		IClass clazz = _reflector.forObject(elements[0]);
		Object obj = array.newInstance(clazz,0);
		_assert(obj != null, "Creation of zero length array");
		_assert(array.getLength(obj) == 0, "Zero length array length");
		obj = array.newInstance(clazz, elements.length);
		_assert(obj != null, "Creation of variable length array");
		_assert(array.getLength(obj) == elements.length, "Variable length array length");
		for (int i = 0; i < elements.length; i++) {
			array.set(obj, i, elements[i]);
		}
		for (int i = 0; i < elements.length; i++) {
			_assert(elements[i].equals(array.get(obj, i)), "Array element comparison");
		}
	}
	
	private void tstIArray2(Object arr){
		IArray array = _reflector.array();
		Object element = array.get(arr, 0);
		IClass clazz = _reflector.forObject(element);
		Object obj = array.newInstance(clazz,0);
		_assert(obj != null, "Creation of zero length array");
		_assert(array.getLength(obj) == 0, "Zero length array length");
		int length = array.getLength(arr);
		obj = array.newInstance(clazz, length);
		_assert(obj != null, "Creation of variable length array");
		_assert(array.getLength(obj) == length, "Variable length array length");
		for (int i = 0; i < length; i++) {
			array.set(obj, i, array.get(arr,i));
		}
		for (int i = 0; i < length; i++) {
			_assert(array.get(arr,i).equals(array.get(obj, i)), "Array element comparison");
		}
	}

}
