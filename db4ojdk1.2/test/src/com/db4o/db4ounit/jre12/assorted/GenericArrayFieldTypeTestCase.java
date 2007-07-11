package com.db4o.db4ounit.jre12.assorted;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.io.*;
import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;
import com.db4o.test.util.*;

import db4ounit.*;

public class GenericArrayFieldTypeTestCase implements TestLifeCycle {
	
	public static class SubData {
		public int _id;

		public SubData(int id) {
			_id = id;
		}
	}
	
	public static class Data {
		public SubData[] _data;

		public Data(SubData[] data) {
			_data = data;
		}
	}

	private static final String FILENAME = "genericarray.db4o";
	
	public void testGenericArrayFieldType() {
		Class[] excludedClasses = new Class[]{
				Data.class,
				SubData.class,
		};
		ClassLoader loader = new ExcludingClassLoader(getClass().getClassLoader(), excludedClasses);
		Configuration config = Db4o.newConfiguration();
		config.reflectWith(new JdkReflector(loader));
		ObjectContainer db = Db4o.openFile(config, FILENAME);
		try {
			ReflectClass dataClazz = db.ext().reflector().forName(Data.class.getName());
			ReflectField field = dataClazz.getDeclaredField("_data");
			ReflectClass fieldType = field.getFieldType();
			Assert.isTrue(fieldType.isArray());
			ReflectClass componentType = fieldType.getComponentType();
			Assert.areEqual(SubData.class.getName(), componentType.getName());
		}
		finally {
			db.close();
		}
	}

	private void store() {
		ObjectContainer db = Db4o.openFile(Db4o.newConfiguration(), FILENAME);
		SubData[] subData = {
			new SubData(1),
			new SubData(42),
		};
		Data data = new Data(subData);
		db.set(data);
		db.close();
	}

	private void deleteFile() {
		File4.delete(FILENAME);
	}

	public void setUp() throws Exception {
		deleteFile();
		store();
	}

	public void tearDown() throws Exception {
		deleteFile();
	}
}
