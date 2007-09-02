/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;

import com.db4o.ObjectContainer;
import com.db4o.activation.Activator;
import com.db4o.instrumentation.BloatInstrumentingClassLoader;
import com.db4o.instrumentation.ByNameClassFilter;
import com.db4o.instrumentation.ClassFilter;
import com.db4o.ta.Activatable;
import com.db4o.ta.instrumentation.InjectTransparentActivationEdit;
import com.db4o.ta.instrumentation.TransparentActivationInstrumentationConstants;

import db4ounit.Assert;
import db4ounit.CodeBlock;
import db4ounit.TestLifeCycle;

public class TransparentActivationClassLoaderTestCase implements TestLifeCycle {

	private static final Class ORIG_CLASS = ToBeInstrumented.class;
	private static final String CLASS_NAME = ORIG_CLASS.getName();
	private static final Class SUB_CLASS = ToBeInstrumentedSub.class;
	private static final String SUB_CLASS_NAME = SUB_CLASS.getName();

	private ClassLoader _loader;

	public void testSelectedClassIsInstrumented() throws Exception {
		Class clazz = _loader.loadClass(CLASS_NAME);
		Assert.areEqual(CLASS_NAME, clazz.getName());
		Assert.areNotSame(ORIG_CLASS, clazz);
		assertActivatableInterface(clazz);
		assertActivatorField(clazz);		
		assertBindMethod(clazz);
		assertActivateMethod(clazz);
		assertMethodInstrumentation(clazz, "foo", true);
		assertMethodInstrumentation(clazz, "bar", true);
		assertMethodInstrumentation(clazz, "baz", true);
		assertMethodInstrumentation(clazz, "boo", true);
	}

	public void testSubClassIsInstrumented() throws Exception {
		Class clazz = _loader.loadClass(SUB_CLASS_NAME);
		Assert.areEqual(SUB_CLASS_NAME, clazz.getName());
		Assert.areNotSame(SUB_CLASS, clazz);
		assertNoActivatorField(clazz);
		assertNoMethod(clazz, TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Class[]{ ObjectContainer.class });
		assertNoMethod(clazz, TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Class[]{});
		assertMethodInstrumentation(clazz, "fooSub", true);
		assertMethodInstrumentation(clazz, "barSub", true);
		assertMethodInstrumentation(clazz, "bazSub", true);
		assertMethodInstrumentation(clazz, "booSub", true);
	}

	private void assertActivatableInterface(Class clazz) {
		Assert.isTrue(Activatable.class.isAssignableFrom(clazz));
	}

	private void assertActivatorField(Class clazz) throws NoSuchFieldException {
		Field activatorField = clazz.getDeclaredField(TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		Assert.areEqual(Activator.class, activatorField.getType());
		assertFieldModifier(activatorField, Modifier.PRIVATE);
		assertFieldModifier(activatorField, Modifier.TRANSIENT);
	}

	private void assertNoActivatorField(final Class clazz) {
		Assert.expect(NoSuchFieldException.class, new CodeBlock() {
			public void run() throws Throwable {
				clazz.getDeclaredField(TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
			}
		});
	}

	private void assertBindMethod(Class clazz) throws Exception {
		final Field activatorField = clazz.getDeclaredField(TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		final Method bindMethod = clazz.getDeclaredMethod(TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Class[]{Activator.class});
		Assert.isTrue((bindMethod.getModifiers() & Modifier.PUBLIC) > 0);
		
		activatorField.setAccessible(true);
		final Object obj = clazz.newInstance();
		Assert.isNull(activatorField.get(obj));
		
		MockActivator oc = new MockActivator(); 
		Assert.areEqual(0, oc.count());
		
		bindMethod.invoke(obj, new Object[]{ oc });
		Object activator = activatorField.get(obj);
		Assert.isNotNull(activator);
		
		try {
			bindMethod.invoke(obj, new Object[]{ oc });
			Assert.fail();
		} catch (InvocationTargetException x) {
			Assert.isInstanceOf(IllegalStateException.class, x.getTargetException());
		}
		
		MockActivator otherOc = new MockActivator();
		try {
			bindMethod.invoke(obj, new Object[]{ otherOc });
			Assert.fail();
		}
		catch(InvocationTargetException exc) {
			Assert.isInstanceOf(IllegalStateException.class, exc.getTargetException());
		}
		Assert.areEqual(0, oc.count());
		Assert.areEqual(0, otherOc.count());
	}

	private void assertNoMethod(final Class clazz,final String methodName,final Class[] paramTypes) throws Exception {
		Assert.expect(NoSuchMethodException.class, new CodeBlock() {
			public void run() throws Throwable {
				clazz.getDeclaredMethod(methodName, paramTypes);
			}
		});
	}
	
	private void assertActivateMethod(Class clazz) throws Exception {
		final Method activateMethod = clazz.getDeclaredMethod(TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Class[]{});
		activateMethod.setAccessible(true);
		Assert.isTrue((activateMethod.getModifiers() & Modifier.PROTECTED) > 0);
		final Activatable obj = (Activatable) clazz.newInstance();
		MockActivator activator = new MockActivator();
		obj.bind(activator);
		activateMethod.invoke(obj, new Object[]{});
		activateMethod.invoke(obj, new Object[]{});
		Assert.areEqual(2, activator.count());
	}

	private void assertMethodInstrumentation(Class clazz,String methodName,boolean expectInstrumentation) throws Exception {
		final Activatable obj = (Activatable) clazz.newInstance();
		MockActivator oc = new MockActivator();
		obj.bind(oc);
		final Method method = clazz.getDeclaredMethod(methodName, new Class[]{});
		method.setAccessible(true);
		method.invoke(obj, new Object[]{});
		if (expectInstrumentation) {
			Assert.areEqual(1, oc.count());
		} else {
			Assert.areEqual(0, oc.count());
		}
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
		ClassFilter filter = new ByNameClassFilter(new String[]{ CLASS_NAME, SUB_CLASS_NAME });
		_loader = new BloatInstrumentingClassLoader(urls, baseLoader, filter, new InjectTransparentActivationEdit(filter));
	}

	public void tearDown() throws Exception {
	}
	
}
