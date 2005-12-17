package com.db4o.reflect.self;

import junit.framework.*;

import com.db4o.reflect.*;

public class SelfReflectTest extends TestCase {
	private SelfReflectionRegistry _registry;
	private SelfReflector _reflector;

	protected void setUp() throws Exception {
		_registry=new UnitDogSelfReflectionRegistry();
		_reflector = new SelfReflector(_registry);
		_reflector.setParent(_reflector);
	}

	public void testReflectorClassRetrieval() {
		assertSelfClass(Dog.class, _reflector.forClass(Dog.class));
		assertSelfClass(Dog.class, _reflector.forName(Dog.class.getName()));
		assertSelfClass(Dog.class, _reflector.forObject(new Dog("Laika",7)));
	}

	public void testSelfClass() throws Exception {
		SelfClass selfClass = selfclass();
		assertEquals(Dog.class.getName(), selfClass.getName());
		assertEquals(Dog.class,selfClass.getJavaClass());
		assertFalse(selfClass.isAbstract());
		assertFalse(selfClass.isInterface());
		assertFalse(selfClass.isPrimitive());
		
		SelfClass superClass = (SelfClass)selfClass.getSuperclass();
		assertEquals(Animal.class.getName(), superClass.getName());
		assertEquals(Animal.class,superClass.getJavaClass());
		assertEquals(Object.class.getName(),superClass.getSuperclass().getName());
		assertNull(superClass.getSuperclass().getSuperclass());
		assertTrue(superClass.isAbstract());
		assertFalse(superClass.isInterface());
		assertFalse(superClass.isPrimitive());
		
		assertTrue(selfClass.isAssignableFrom(selfClass));
		assertTrue(superClass.isAssignableFrom(selfClass));
		assertFalse(selfClass.isAssignableFrom(superClass));

		assertTrue(selfClass.isInstance(new Dog()));
		assertTrue(superClass.isInstance(new Dog()));
		assertFalse(selfClass.isInstance(new Object()));
		
		ReflectField[] fields = selfClass.getDeclaredFields();
		assertEquals(1, fields.length);
		assertEquals(selfClass, selfClass.getDelegate());
	}

	public void testInterface() {
		SelfClass interfaceClass=(SelfClass)_reflector.forClass(Being.class);
		assertEquals(Being.class.getName(),interfaceClass.getName());
		assertEquals(Being.class,interfaceClass.getJavaClass());
		assertTrue(interfaceClass.isInterface());
		assertTrue(interfaceClass.isAbstract());
		assertFalse(interfaceClass.isPrimitive());
		assertEquals(0,interfaceClass.getDeclaredFields().length);
		assertEquals(0,interfaceClass.getDeclaredConstructors().length);
		
		SelfClass selfClass=selfclass();
		assertTrue(interfaceClass.isAssignableFrom(selfClass));
		assertFalse(selfClass.isAssignableFrom(interfaceClass));
		assertTrue(interfaceClass.isInstance(new Dog()));
	}

	public void testPrimitive() {
		Class[] primitives={Integer.class,Long.class,Float.class,Double.class,Short.class,Character.class,Byte.class,String.class};
		for(int idx=0;idx<primitives.length;idx++) {
			assertPrimitive(primitives[idx]);
		}
		assertFalse(_reflector.forClass(Object.class).isPrimitive());
	}
	
	public void testSelfField() {
		SelfClass selfClass = selfclass();
		ReflectField field = selfClass.getDeclaredField("_age");
		assertEquals(Integer.class.getName(),field.getType().getName());
		assertEquals("_age", field.getName());
	}
	
	public void testInstanceField() {
		Dog laika=new Dog("Laika",7);
		ReflectField field = selfclass().getDeclaredField("_age");
		Object value=field.get(laika);
		assertEquals(new Integer(laika.age()),value);
		field.set(laika, new Integer(8));
		assertEquals(laika.age(),8);
		assertEquals(field.get(laika),new Integer(8));
	}

	private SelfClass selfclass() {
		SelfClass selfClass = new SelfClass(_reflector,_registry, Dog.class);
		return selfClass;
	}

	private void assertSelfClass(Class jdkClass, ReflectClass reflectClass) {
		SelfClass selfClass = (SelfClass) reflectClass;
		assertEquals(jdkClass.getName(), selfClass.getName());
	}

	private void assertPrimitive(Class clazz) {
		SelfClass primitiveClass=(SelfClass)_reflector.forClass(clazz);
		assertFalse(primitiveClass.isInterface());
		assertFalse(primitiveClass.isAbstract());
		assertTrue(primitiveClass.isPrimitive());
	}
}
