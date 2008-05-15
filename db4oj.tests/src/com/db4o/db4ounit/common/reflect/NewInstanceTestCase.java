package com.db4o.db4ounit.common.reflect;

import java.util.*;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

import db4ounit.*;

public class NewInstanceTestCase implements TestCase, TestLifeCycle {
	
	private Reflector _reflector;
	
	public static class ItemNoPublicConstructors {
		private ItemNoPublicConstructors(){}
	}
	
	private static class ItemThrowingConstructors {
		public ItemThrowingConstructors() {
			throw new RuntimeException();
		}
		
		public ItemThrowingConstructors(int value) {
			throw new RuntimeException();
		}
	} 
	
	public static class ItemNoDefaultConstructor {
		public ItemNoDefaultConstructor(int value) {
		}
	}
	
	public static class ItemParent {
		public ItemChild _child;
		
		public ItemParent(ItemChild child) {
			_child = child;
		}
		
	}
	
	public static class ItemChild {
		public String _name;
		
		public ItemChild(String name) {
			_name = name;
		}
	}
	
		
	public static class MockReflectorConfiguration implements ReflectorConfiguration {

		private List _classNames;
		
		private boolean _testConstructor;
		
		public MockReflectorConfiguration(String[] classNames) {
			this(classNames, true);
		}
		
		public MockReflectorConfiguration(String[] classNames, boolean testConstructor) {
			_classNames = Arrays.asList(classNames);
			_testConstructor = testConstructor;
		}
		
		public boolean callConstructor(ReflectClass clazz) {
			return _classNames.contains(clazz.getName());
		}

		public boolean testConstructors() {
			return _testConstructor;
		}
		
	}
	
	public void testComplexItem() throws Exception {
		ReflectClass parentClazz = _reflector.forObject(new ItemParent(null));
		ReflectField[] fields = parentClazz.getDeclaredFields();
		Assert.areEqual(1, fields.length);

		ReflectClass fieldClazz = fields[0].getFieldType();
		ReflectClass childClazz = _reflector.forClass(ItemChild.class);
		Assert.areEqual(childClazz.getName(), fieldClazz.getName());
		
	}
	
	public void testNotStorable() throws Exception {
//		assertCannotBeInstantiated(ItemThrowingConstructors.class);
//		assertCannotBeInstantiated(ItemNoPublicConstructors.class);
		assertCannotBeInstantiated(List.class);
		if(!Deploy.csharp){
			assertCannotBeInstantiated(Dictionary.class);
		}
	}
	
	public void testNoDefaultConstructor() throws Exception {
		Assert.isNotNull(createInstanceOf(ItemNoDefaultConstructor.class));		
	}
	
	public void assertCannotBeInstantiated(Class clazz) {
		ReflectClass reflectClass = _reflector.forClass(clazz);
		Assert.isFalse(reflectClass.ensureCanBeInstantiated());
		Assert.isNull(reflectClass.newInstance());
	}
	
	private boolean classCanBeInitialized(Class clazz) {
		return _reflector.forClass(clazz).ensureCanBeInstantiated();
	}
	
	public void testHashTable() throws Exception {
		Hashtable hashTable = (Hashtable)createInstanceOf(Hashtable.class);
		assertIsUsable(hashTable);
	}
	
	public void testHashMap() throws Exception {
		HashMap hashMap = (HashMap)createInstanceOf(HashMap.class);
		assertIsUsable(hashMap);
	}

	public void testList() throws Exception {
		List list = (List)createInstanceOf(ArrayList.class);
		assertIsUsable(list);
	}
	
	public void testFloat() throws Exception {
		Float f = (Float)createInstanceOf(Float.class);
		assertIsUsable(f);
	}
	
	public void testString() throws Exception {
		String s = (String)createInstanceOf(String.class);
		assertIsUsable(s);
	}
	
	private void assertIsUsable(Float f) {
		Assert.areEqual(0.0, f.floatValue());
	}
	
	private void assertIsUsable(String s) {
		Assert.areEqual(0, s.length());
	}

	private void assertIsUsable(Collection collection) {
		if(!Deploy.csharp) {
			Assert.isTrue(collection.isEmpty());
			
			collection.add(new Integer(1));
			Assert.areEqual(1, collection.size());
			
			Assert.isTrue(collection.contains(new Integer(1)));
			
			collection.clear();
		}
		Assert.areEqual(0, collection.size());
		
	}
	
	private void assertIsUsable(Map map) {
		if(!Deploy.csharp) {
			Assert.isTrue(map.isEmpty());
		}
		
		map.put(new Integer(1), "one");
		Assert.areEqual(1, map.size());
		
		Assert.areEqual("one", map.get(new Integer(1)));
		
		map.remove(new Integer(1));
		Assert.areEqual(0, map.size());
	}
	

	private Object createInstanceOf(Class clazz) {
		return _reflector.forClass(clazz).newInstance();
	}
	
	public void setUp() throws Exception {
		_reflector = Platform4.reflectorForType(this.getClass());
//		_reflector.setParent(_reflector);
		String[] clazzs = new String[]{
				_reflector.forClass(ItemThrowingConstructors.class).getName(),
				_reflector.forClass(ItemNoPublicConstructors.class).getName(),
				_reflector.forClass(Hashtable.class).getName(),
				_reflector.forClass(HashMap.class).getName(),
				_reflector.forClass(ArrayList.class).getName(),
				_reflector.forClass(Integer.class).getName(),
				_reflector.forClass(Float.class).getName(),
				_reflector.forClass(String.class).getName(),
		};
		MockReflectorConfiguration config = new MockReflectorConfiguration(clazzs, true);
		_reflector.configuration(config);
	}

	public void tearDown() throws Exception {
	}

}
