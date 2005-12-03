package com.db4o.reflect.self;

import junit.framework.*;

import com.db4o.reflect.*;

public class SelfReflectTest extends TestCase {
	private SelfReflector _reflector;

	protected void setUp() throws Exception {
		_reflector = new SelfReflector(new UnitDogSelfReflectionRegistry());
	}

	public void testReflectorClassRetrieval() {
		assertSelfClass(Dog.class, _reflector.forClass(Dog.class));
		assertSelfClass(Dog.class, _reflector.forName(Dog.class.getName()));
		assertSelfClass(Dog.class, _reflector.forObject(new Dog("Laika")));
	}

	public void testSelfClass() {
		SelfClass selfClass = selfclass();
		assertEquals(Dog.class.getName(), selfClass.getName());
		assertEquals(Object.class.getName(), selfClass.getSuperclass()
				.getName());
		ReflectField[] fields = selfClass.getDeclaredFields();
		assertEquals(1, fields.length);
		ReflectField field = selfClass.getDeclaredField("_name");
		try {
			assertEquals(Dog.class.getDeclaredField("_name"), field);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		assertEquals(selfClass.reflector(), selfClass.getDelegate());
		assertSame(_reflector, selfClass.reflector());
	}

	public void testSelfField() {
		SelfClass selfClass = selfclass();
		ReflectField field = selfClass.getDeclaredField("_name");
		SelfField selfField = new SelfField("_name", String.class);
		assertEquals(field.getType(), selfField.getType());
		assertEquals("_name", selfField.getName());
	}

	private SelfClass selfclass() {
		SelfClass selfClass = new SelfClass(_reflector, Dog.class);
		return selfClass;
	}

	private void assertSelfClass(Class jdkClass, ReflectClass reflectClass) {
		SelfClass selfClass = (SelfClass) reflectClass;
		assertEquals(jdkClass.getName(), selfClass.getName());
		// TODO: Check semantics for reflector() - why does it return the
		// parent?
		// assertSame(_reflector, selfClass.reflector());
	}
}
