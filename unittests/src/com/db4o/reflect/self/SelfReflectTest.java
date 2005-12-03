package com.db4o.reflect.self;

import junit.framework.*;

import com.db4o.reflect.*;

public class SelfReflectTest extends TestCase {
	private SelfReflectionRegistry _registry;
	private SelfReflector _reflector;

	protected void setUp() throws Exception {
		_registry=new UnitDogSelfReflectionRegistry();
		_reflector = new SelfReflector(_registry);
	}

	public void testReflectorClassRetrieval() {
		assertSelfClass(Dog.class, _reflector.forClass(Dog.class));
		assertSelfClass(Dog.class, _reflector.forName(Dog.class.getName()));
		assertSelfClass(Dog.class, _reflector.forObject(new Dog("Laika")));
	}

	public void testSelfClass() throws Exception {
		SelfClass selfClass = selfclass();
		assertEquals(Dog.class.getName(), selfClass.getName());
		assertEquals(Object.class.getName(), selfClass.getSuperclass()
				.getName());
		assertNull(selfClass.getSuperclass().getSuperclass());
		ReflectField[] fields = selfClass.getDeclaredFields();
		assertEquals(1, fields.length);
		assertEquals(selfClass, selfClass.getDelegate());
		assertSame(_reflector, selfClass.reflector());
	}

	public void testSelfField() {
		SelfClass selfClass = selfclass();
		ReflectField field = selfClass.getDeclaredField("_name");
		assertEquals(String.class.getName(),field.getType().getName());
		assertEquals("_name", field.getName());
	}
	
	public void testInstanceField() {
		Dog laika=new Dog("Laika");
		ReflectField field = selfclass().getDeclaredField("_name");
		Object value=field.get(laika);
		assertEquals(laika.name(),value);
		field.set(laika, "Lassie");
		assertEquals(laika.name(),"Lassie");
		assertEquals(field.get(laika),"Lassie");
	}

	private SelfClass selfclass() {
		SelfClass selfClass = new SelfClass(_reflector,_registry, Dog.class);
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
