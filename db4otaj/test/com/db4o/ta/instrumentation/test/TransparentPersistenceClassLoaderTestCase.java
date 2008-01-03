/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

import java.lang.reflect.*;
import java.net.*;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;

import db4ounit.*;

public class TransparentPersistenceClassLoaderTestCase implements TestLifeCycle {

	private static final Class ORIG_CLASS = ToBeInstrumented.class;	
	private static final String CLASS_NAME = ORIG_CLASS.getName();
	private static final Class SUB_CLASS = ToBeInstrumentedSub.class;
	private static final String SUB_CLASS_NAME = SUB_CLASS.getName();
	private static final Class FA_CLASS = ToBeInstrumentedWithFieldAccess.class;
	private static final String FA_CLASS_NAME = FA_CLASS.getName();
	private static final Class NI_CLASS = NotToBeInstrumented.class;
	private static final String NI_CLASS_NAME = NI_CLASS.getName();
	private static final Class CNI_CLASS = CanNotBeInstrumented.class;
	private static final String CNI_CLASS_NAME = CNI_CLASS.getName();

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
		assertMethodInstrumentation(clazz, "fooTransient", false);
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

	public void testFieldAccessIsInstrumented() throws Exception {
		final Activatable objOne = newToBeInstrumentedInstance();
		final Activatable objTwo = newToBeInstrumentedInstance();
		MockActivator ocOne = activatorFor(objOne);
		MockActivator ocTwo = activatorFor(objTwo);
		invoke(objOne, "compareID", new Class[]{ objTwo.getClass() }, new Object[]{ objTwo });
		Assert.areEqual(1, ocOne.count());
		Assert.areEqual(1, ocTwo.count());
	}

	public void testFieldSetterIsInstrumented() throws Exception {
		final Activatable obj = newToBeInstrumentedInstance();
		final MockActivator activator = activatorFor(obj);		
		invoke(obj, "setId", new Class[] { Integer.TYPE }, new Object[] { new Integer(42) });
		Assert.areEqual(1, activator.writeCount());
		Assert.areEqual(0, activator.readCount());
	}

	public void testInterObjectFieldAccessIsInstrumented() throws Exception {
		Class iClazz = _loader.loadClass(CLASS_NAME);
		Class niClazz = _loader.loadClass(NI_CLASS_NAME);
		final Activatable iObj = (Activatable) iClazz.newInstance();
		final Object niObj = niClazz.newInstance();
		MockActivator act = activatorFor(iObj);
		invoke(niObj, "accessToBeInstrumented", new Class[]{ iClazz }, new Object[]{ iObj });
		Assert.areEqual(1, act.count());
	}
	
	private Object invoke(final Object obj, String methodName,
			Class[] signature, Object[] arguments)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		final Method method = obj.getClass().getDeclaredMethod(methodName, signature);
		method.setAccessible(true);
		return method.invoke(obj, arguments);
	}

	private MockActivator activatorFor(final Activatable obj) {
		MockActivator activator = new MockActivator();
		obj.bind(activator);
		return activator;
	}

	private Activatable newToBeInstrumentedInstance()
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class clazz = _loader.loadClass(FA_CLASS_NAME);
		return (Activatable) clazz.newInstance();
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
		final Method activateMethod = clazz.getDeclaredMethod(TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Class[]{ActivationPurpose.class});
		activateMethod.setAccessible(true);
		Assert.isTrue((activateMethod.getModifiers() & Modifier.PUBLIC) > 0);
		final Activatable obj = (Activatable) clazz.newInstance();
		MockActivator activator = activatorFor(obj);
		activateMethod.invoke(obj, new Object[]{ActivationPurpose.READ});
		activateMethod.invoke(obj, new Object[]{ActivationPurpose.READ});
		Assert.areEqual(2, activator.count());
	}

	private void assertMethodInstrumentation(Class clazz,String methodName,boolean expectInstrumentation) throws Exception {
		final Activatable obj = (Activatable) clazz.newInstance();
		MockActivator oc = activatorFor(obj);
		invoke(obj, methodName, new Class[]{}, new Object[]{});
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
		Class clazz = _loader.loadClass(NI_CLASS_NAME);
		Assert.areEqual(NI_CLASS_NAME, clazz.getName());
		Assert.areNotSame(NI_CLASS, clazz);
		Assert.isFalse(Activatable.class.isAssignableFrom(clazz));
	}

	public void testCanNotBeInstrumented() throws Exception {
		Class clazz = _loader.loadClass(CNI_CLASS_NAME);
		Assert.isFalse(Activatable.class.isAssignableFrom(clazz));
	}
	
	public void setUp() throws Exception {
		ClassLoader baseLoader = ORIG_CLASS.getClassLoader();
		URL[] urls = {};
		ClassFilter filter = new ByNameClassFilter(new String[]{ CLASS_NAME, SUB_CLASS_NAME, FA_CLASS_NAME, CNI_CLASS_NAME });
		_loader = new BloatInstrumentingClassLoader(urls, baseLoader, new AcceptAllClassesFilter(), new InjectTransparentActivationEdit(filter));
	}

	public void tearDown() throws Exception {
	}
	
	public static void main(String[] args) {
		new TestRunner(TransparentPersistenceClassLoaderTestCase.class).run();
	}
}
