package com.db4o.reflect.self;

import junit.framework.*;

import com.db4o.reflect.*;

public class SelfReflectTest extends TestCase {
	private SelfReflectionRegistry _registry;

	private SelfReflector _reflector;

	protected void setUp() throws Exception {
		_registry = new UnitDogSelfReflectionRegistry();
		_reflector = new SelfReflector(_registry);
		_reflector.setParent(_reflector);
	}

	public void testReflectorClassRetrieval() {
		assertSelfClass(Dog.class, _reflector.forClass(Dog.class));
		assertSelfClass(Dog.class, _reflector.forName(Dog.class.getName()));
		assertSelfClass(Dog.class, _reflector.forObject(new Dog("Laika", 7,new Dog[0],new int[0])));
	}

	public void testSelfClass() throws Exception {
		SelfClass selfClass = selfclass();
		assertEquals(Dog.class.getName(), selfClass.getName());
		assertEquals(Dog.class, selfClass.getJavaClass());
		assertFalse(selfClass.isAbstract());
		assertFalse(selfClass.isInterface());
		assertFalse(selfClass.isPrimitive());
		assertNull(selfClass.getComponentType());

		SelfClass superClass = (SelfClass) selfClass.getSuperclass();
		assertEquals(Animal.class.getName(), superClass.getName());
		assertEquals(Animal.class, superClass.getJavaClass());
		assertEquals(Object.class.getName(), superClass.getSuperclass()
				.getName());
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
		assertEquals(3, fields.length);
		assertEquals(selfClass, selfClass.getDelegate());
	}

	public void testInterface() {
		SelfClass interfaceClass = (SelfClass) _reflector.forClass(Being.class);
		assertEquals(Being.class.getName(), interfaceClass.getName());
		assertEquals(Being.class, interfaceClass.getJavaClass());
		assertTrue(interfaceClass.isInterface());
		assertTrue(interfaceClass.isAbstract());
		assertFalse(interfaceClass.isPrimitive());
		assertEquals(0, interfaceClass.getDeclaredFields().length);
		assertEquals(0, interfaceClass.getDeclaredConstructors().length);

		SelfClass selfClass = selfclass();
		assertTrue(interfaceClass.isAssignableFrom(selfClass));
		assertFalse(selfClass.isAssignableFrom(interfaceClass));
		assertTrue(interfaceClass.isInstance(new Dog()));
	}

	public void testPrimitive() {
		Class[] primitives = { Integer.class, Long.class, Short.class,
				Character.class, Byte.class, String.class };
		for (int idx = 0; idx < primitives.length; idx++) {
			assertPrimitive(primitives[idx]);
		}
		assertFalse(_reflector.forClass(Object.class).isPrimitive());
	}

	public void testSelfField() {
		SelfClass selfClass = selfclass();
		ReflectField field = selfClass.getDeclaredField("_age");
		assertEquals(Integer.class.getName(), field.getType().getName());
		assertEquals("_age", field.getName());

		assertTrue(selfClass.getDeclaredField("_age").isPublic());
		assertFalse(selfClass.getDeclaredField("_age").isStatic());
		assertFalse(selfClass.getDeclaredField("_age").isTransient());

		assertTrue(selfClass.getDeclaredField("_parents").isPublic());
		assertFalse(selfClass.getDeclaredField("_parents").isStatic());
		assertFalse(selfClass.getDeclaredField("_parents").isTransient());

		SelfClass superClass = (SelfClass) selfClass.getSuperclass();
		assertFalse(superClass.getDeclaredField("_name").isPublic());
		assertFalse(superClass.getDeclaredField("_name").isStatic());
		assertFalse(superClass.getDeclaredField("_name").isTransient());

	}

	public void testInstanceField() {
		Dog laika = new Dog("Laika", 7, new Dog[0],new int[0]);
		ReflectField field = selfclass().getDeclaredField("_age");
		Object value = field.get(laika);
		assertEquals(new Integer(laika.age()), value);
		field.set(laika, new Integer(8));
		assertEquals(laika.age(), 8);
		assertEquals(field.get(laika), new Integer(8));
	}

	public void testArray() {
		SelfClass arrayClass = (SelfClass) _reflector.forClass(Dog[].class);
		assertTrue(arrayClass.isArray());
		assertEquals(_reflector.forClass(Dog.class).getName(), arrayClass
				.getComponentType().getName());

		SelfArray arrayHandler = (SelfArray) _reflector.array();
		assertEquals(_reflector.forClass(Dog.class).getName(), arrayHandler
				.getComponentType(arrayClass).getName());
		Dog[] dogs = (Dog[]) arrayHandler.newInstance(_reflector
				.forClass(Dog.class), 2);
		assertEquals(2, dogs.length);
		dogs = (Dog[]) arrayHandler.newInstance(_reflector.forClass(Dog.class), new int[]{2});
		assertEquals(2, dogs.length);
		assertFalse(arrayHandler.isNDimensional(arrayClass));
		assertEquals(2,arrayHandler.getLength(dogs));
		int[] dim=arrayHandler.dimensions(dogs);
		assertEquals(1,dim.length);
		assertEquals(dogs.length,dim[0]);
		
		dogs[0]=new Dog("Laika",7,new Dog[0],new int[0]);
		dogs[1]=new Dog("Lassie",6,new Dog[0],new int[0]);
		assertEquals(dogs[1],arrayHandler.get(dogs,1));	
		Dog sharik = new Dog("Sharik",100,new Dog[0],new int[0]);
		arrayHandler.set(dogs,1,sharik);
		assertEquals(sharik,arrayHandler.get(dogs,1));	
		
		Object[] flattened=new Object[2];
		int numFlat=arrayHandler.flatten(dogs, new int[]{2}, 0, flattened, 0);
		assertEquals(dogs.length,numFlat);
		for (int i = 0; i < dogs.length; i++) {
			assertEquals(dogs[i],flattened[i]);
		}
		int numShape=arrayHandler.shape(flattened, 0, dogs, new int[]{2}, 0);
		assertEquals(flattened.length,numShape);
		for (int i = 0; i < dogs.length; i++) {
			assertEquals(flattened[i],dogs[i]);
		}
	}

	public void testPrimitiveArray() {
		SelfClass arrayClass = (SelfClass) _reflector.forClass(int[].class);
		assertTrue(arrayClass.isArray());
		assertEquals(_reflector.forClass(Integer.class).getName(), arrayClass
				.getComponentType().getName());
		SelfArray arrayHandler = (SelfArray) _reflector.array();
		assertEquals(_reflector.forClass(Integer.class).getName(), arrayHandler
				.getComponentType(arrayClass).getName());
		int[] prices = (int[]) arrayHandler.newInstance(_reflector.forClass(Integer.class),2);
		assertEquals(2, prices.length);
		prices = (int[]) arrayHandler.newInstance(_reflector.forClass(int.class), new int[]{2});
		assertEquals(2, prices.length);

		assertFalse(arrayHandler.isNDimensional(arrayClass));
		assertEquals(prices.length,arrayHandler.getLength(prices));
		int[] dim=arrayHandler.dimensions(prices);
		assertEquals(1,dim.length);
		assertEquals(prices.length,dim[0]);

		prices[0]=3;
		prices[1]=1;
		assertEquals(new Integer(prices[1]),arrayHandler.get(prices,1));	
		arrayHandler.set(prices,1,new Integer(2));
		assertEquals(2,prices[1]);
		assertEquals(new Integer(2),arrayHandler.get(prices,1));	
		
		Object[] flattened=new Object[2];
		int numFlat=arrayHandler.flatten(prices, new int[]{2}, 0, flattened, 0);
		assertEquals(prices.length,numFlat);
		for (int i = 0; i < prices.length; i++) {
			assertEquals(new Integer(prices[i]),flattened[i]);
		}
		int numShape=arrayHandler.shape(flattened, 0, prices, new int[]{2}, 0);
		assertEquals(flattened.length,numShape);
		for (int i = 0; i < prices.length; i++) {
			assertEquals(flattened[i],new Integer(prices[i]));
		}

	}
	
	private SelfClass selfclass() {
		SelfClass selfClass = new SelfClass(_reflector, _registry, Dog.class);
		return selfClass;
	}

	private void assertSelfClass(Class jdkClass, ReflectClass reflectClass) {
		SelfClass selfClass = (SelfClass) reflectClass;
		assertEquals(jdkClass.getName(), selfClass.getName());
	}

	private void assertPrimitive(Class clazz) {
		SelfClass primitiveClass = (SelfClass) _reflector.forClass(clazz);
		assertFalse(primitiveClass.isInterface());
		assertFalse(primitiveClass.isAbstract());
		assertTrue(clazz.getName(), primitiveClass.isPrimitive());
	}
}
