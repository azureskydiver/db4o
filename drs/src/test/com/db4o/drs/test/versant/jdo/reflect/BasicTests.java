package com.db4o.drs.test.versant.jdo.reflect;

import static db4ounit.Assert.*;

import com.db4o.drs.test.versant.jdo.reflect.JdoAwareItem.*;
import com.db4o.drs.versant.jdo.reflect.*;
import com.db4o.reflect.*;

import db4ounit.*;

public class BasicTests implements TestLifeCycle {
	
	JdoReflector reflector;

	public void setUp() throws Exception {
		reflector = new JdoReflector(getClass().getClassLoader());
		Meta.invocations.clear();
	}
	
	public void tearDown() throws Exception {
	}

	public void testFieldInformation() {
		
		ReflectClass c = reflector.forClass(JdoAwareItem.class);
		isNotNull(c);
		
		ReflectField[] declaredFields = c.getDeclaredFields();
		isNotNull(declaredFields);
		areEqual(3, declaredFields.length);
		
		assertDeclaredField(c, "name", String.class);
		assertDeclaredField(c, "staticField", int.class);
		assertDeclaredField(c, "transientField", int.class);
	}

	private void assertDeclaredField(ReflectClass c, String fieldName, Class<?> fieldType) {
		isNotNull(c.getDeclaredField(fieldName));
		areEqual(reflector.forClass(fieldType).getName(), c.getDeclaredField(fieldName).getFieldType().getName());
	}
	
	public void testGet() {
		
		JdoAwareItem item = new JdoAwareItem("42");

		ReflectClass c = reflector.forObject(item);
		
		isTrue(Meta.invocations.isEmpty());
		areEqual("42", c.getDeclaredField("name").get(item));
		IteratorAssert.areEqual(new String[]{"jdoGetname"}, Meta.invocations.iterator());
	}
	
	public void testSet() {
		
		JdoAwareItem item = new JdoAwareItem();

		ReflectClass c = reflector.forObject(item);
		
		isTrue(Meta.invocations.isEmpty());
		
		c.getDeclaredField("name").set(item, "42");
		areEqual("42", c.getDeclaredField("name").get(item));
		
		IteratorAssert.areEqual(new String[]{"jdoSetname", "jdoGetname"}, Meta.invocations.iterator());

	}
	
}
