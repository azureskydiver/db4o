package com.db4o.ta.instrumentation.test;

import java.lang.reflect.*;
import java.net.*;

import com.db4o.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;
import com.db4o.ta.internal.*;

import db4ounit.*;

public class TransparentActivationClassLoaderTestCase implements TestLifeCycle {

	private static final Class ORIG_CLASS = ToBeInstrumented.class;
	private static final String CLASS_NAME = ORIG_CLASS.getName();

	private ClassLoader _loader;

	
	public void testSelectedClassIsInstrumented() throws Exception {
		Class clazz = _loader.loadClass(CLASS_NAME);
		Assert.areEqual(CLASS_NAME, clazz.getName());
		Assert.areNotSame(ORIG_CLASS, clazz);
		Assert.isTrue(Activatable.class.isAssignableFrom(clazz));
		Field activatorField = clazz.getDeclaredField(TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		Assert.areEqual(Activator.class, activatorField.getType());
		assertFieldModifier(activatorField, Modifier.PRIVATE);
		assertFieldModifier(activatorField, Modifier.TRANSIENT);
		Method bindMethod = clazz.getDeclaredMethod(TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Class[]{ObjectContainer.class});
		Assert.isTrue((bindMethod.getModifiers() & Modifier.PUBLIC) > 0);
		
		activatorField.setAccessible(true);
		Object obj = clazz.newInstance();
		Assert.isNull(activatorField.get(obj));
		bindMethod.invoke(obj, new Object[]{ null });
		Assert.isNotNull(activatorField.get(obj));
	}

	private void assertFieldModifier(Field activatorField, int modifier) {
		Assert.isTrue((activatorField.getModifiers()&modifier)>0);
	}
	
	public void testOtherClassIsNotInstrumented() throws Exception {
		Class otherOrigClass = NotToBeInstrumented.class;
		String otherOrigClassName = otherOrigClass.getName();
		Class clazz = _loader.loadClass(otherOrigClassName);
		Assert.areEqual(otherOrigClassName, clazz.getName());
		Assert.areNotSame(otherOrigClass, clazz);
	}

	public void setUp() throws Exception {
		ClassLoader baseLoader = ORIG_CLASS.getClassLoader();
		URL[] urls = {};
		ClassFilter filter = new ByNameClassFilter(CLASS_NAME);
		_loader = new TransparentActivationClassLoader(urls, baseLoader, filter);
	}

	public void tearDown() throws Exception {
	}
	
}
