package com.db4o.reflect.self;

import junit.framework.*;

import com.db4o.reflect.*;

public class SelfReflectTest extends TestCase {
	private SelfReflector _reflector;
	
	protected void setUp() throws Exception {
		_reflector=new SelfReflector(new UnitDogSelfReflectionRegistry());
	}
	
	public void testReflectorClassRetrieval() {
		assertSelfClass(Dog.class,_reflector.forClass(Dog.class));
		assertSelfClass(Dog.class,_reflector.forName(Dog.class.getName()));
		assertSelfClass(Dog.class,_reflector.forObject(new Dog("Laika")));
	}
	
	public void testSelfClass() {
		SelfClass selfClass=new SelfClass(_reflector,Dog.class);
		assertEquals(Dog.class.getName(),selfClass.getName());
		assertEquals(Object.class.getName(),selfClass.getSuperclass().getName());
		ReflectField[] fields=selfClass.getDeclaredFields();
		assertEquals(1,fields.length);
	}
	
	private void assertSelfClass(Class jdkClass,ReflectClass reflectClass) {
		SelfClass selfClass=(SelfClass)reflectClass;
		assertEquals(jdkClass.getName(), selfClass.getName());
		// TODO: Check semantics for reflector() - why does it return the parent?
		// assertSame(_reflector, selfClass.reflector());
	}
}
