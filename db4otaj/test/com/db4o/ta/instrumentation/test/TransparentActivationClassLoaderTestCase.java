package com.db4o.ta.instrumentation.test;

import java.lang.reflect.*;
import java.net.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.instrumentation.*;
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
		assertActivatableInterface(clazz);
		assertActivatorField(clazz);		
		assertBindMethod(clazz);
		assertActivateMethod(clazz);
		assertMethodInstrumentation(clazz, "foo", true);
		assertMethodInstrumentation(clazz, "bar", true);
		assertMethodInstrumentation(clazz, "baz", true);
		assertMethodInstrumentation(clazz, "boo", false);
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

	private void assertBindMethod(Class clazz) throws Exception {
		final Field activatorField = clazz.getDeclaredField(TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		final Method bindMethod = clazz.getDeclaredMethod(TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Class[]{ObjectContainer.class});
		Assert.isTrue((bindMethod.getModifiers() & Modifier.PUBLIC) > 0);
		
		activatorField.setAccessible(true);
		final Object obj = clazz.newInstance();
		Assert.isNull(activatorField.get(obj));
		
		TransparentActivationMockObjectContainer oc = new TransparentActivationMockObjectContainer(new Iterator4Impl(null)); 
		bindMethod.invoke(obj, new Object[]{ oc });
		Object activator = activatorField.get(obj);
		Assert.isNotNull(activator);
		bindMethod.invoke(obj, new Object[]{ oc });
		Assert.areSame(activator, activatorField.get(obj));
		((Activatable)obj).bind(oc);
		Assert.areSame(activator, activatorField.get(obj));
		
		TransparentActivationMockObjectContainer otherOc = new TransparentActivationMockObjectContainer(new Iterator4Impl(null));
		try {
			bindMethod.invoke(obj, new Object[]{ otherOc });
			Assert.fail();
		}
		catch(InvocationTargetException exc) {
			Assert.isInstanceOf(IllegalStateException.class, exc.getTargetException());
		}
		
		oc.validate();
		otherOc.validate();
	}

	private void assertActivateMethod(Class clazz) throws Exception {
		final Method activateMethod = clazz.getDeclaredMethod(TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Class[]{});
		activateMethod.setAccessible(true);
		Assert.isTrue((activateMethod.getModifiers() & Modifier.PROTECTED) > 0);
		final Activatable obj = (Activatable) clazz.newInstance();
		TransparentActivationMockObjectContainer oc = new TransparentActivationMockObjectContainer(new Iterator4Impl(new List4(obj)));
		obj.bind(oc);
		activateMethod.invoke(obj, new Object[]{});
		activateMethod.invoke(obj, new Object[]{});
		oc.validate();
	}

	private void assertMethodInstrumentation(Class clazz,String methodName,boolean expectInstrumentation) throws Exception {
		final Activatable obj = (Activatable) clazz.newInstance();
		List4 expected = (expectInstrumentation ? new List4(obj) : null);
		TransparentActivationMockObjectContainer oc = new TransparentActivationMockObjectContainer(new Iterator4Impl(expected));
		obj.bind(oc);
		final Method method = clazz.getDeclaredMethod(methodName, new Class[]{});
		method.setAccessible(true);
		method.invoke(obj, new Object[]{});
		oc.validate();
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
		_loader = new BloatInstrumentingClassLoader(urls, baseLoader, filter, new InjectTransparentActivationEdit());
	}

	public void tearDown() throws Exception {
	}
	
}
