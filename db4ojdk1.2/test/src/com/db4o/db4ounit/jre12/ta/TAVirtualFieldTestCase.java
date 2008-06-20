package com.db4o.db4ounit.jre12.ta;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.reflect.generic.*;
import com.db4o.reflect.jdk.*;
import com.db4o.ta.*;
import com.db4o.test.util.*;

import db4ounit.*;

public class TAVirtualFieldTestCase implements TestLifeCycle {
	
	private static String FILEPATH = Path4.getTempFileName();
	
private Db4oUUID _uuid;
	
	public static class Item {
		public Item _next;
	}
	
	public void test() {
		ObjectContainer db = Db4o.openFile(config(true), FILEPATH);
		ObjectSet result = db.query(Item.class);
		Assert.areEqual(1, result.size());
		Object obj = result.next();
		Assert.isInstanceOf(GenericObject.class, obj);
		Assert.areEqual(_uuid, db.ext().getObjectInfo(obj).getUUID());
		db.close();
	}

	public void setUp() throws Exception {
		deleteFile();
		ObjectContainer db = Db4o.openFile(config(false), FILEPATH);
		Item obj = new Item();
		db.store(obj);
		_uuid = db.ext().getObjectInfo(obj).getUUID();
		db.close();
	}

	public void tearDown() throws Exception {
		deleteFile();
	}

	private void deleteFile() {
		new File(FILEPATH).delete();
	}
	
	private Configuration config(boolean withCL) {
		Configuration config = Db4o.newConfiguration();
		config.generateUUIDs(ConfigScope.GLOBALLY);
		config.add(new TransparentActivationSupport());
		if(withCL) {
			ClassLoader cl = new ExcludingClassLoader(Item.class.getClassLoader(), new Class[] { Item.class });
			config.reflectWith(new JdkReflector(cl));
		}
		return config;
	}
}