package com.db4o.browser.model.test;

import java.util.*;

import junit.framework.*;

import com.db4o.*;
import com.db4o.browser.model.*;
import com.db4o.ext.*;

public class MockDatabase extends Assert implements Database {
	private boolean opened=false;
	/** (String)name -> (StoredClass)class */
	private Map storedClasses=new HashMap();
	/** (String)stored class name -> (Object[])instances */
	private Map instances=new HashMap();
	
	public void open(String path) {
		assertFalse("already opened",opened);
		opened=true;
	}

	public void close() {
		assertTrue("not opened",opened);
		opened=false;
	}

	public DatabaseGraphIterator graphIterator() {
		StoredClass[] stored = (StoredClass[])storedClasses.values().toArray(new StoredClass[storedClasses.size()]);
		return new DatabaseGraphIterator(this,stored);
	}

	public DatabaseGraphIterator graphIterator(String name) {
		StoredClass storedclass=(StoredClass)storedClasses.get(name);
		StoredClass[] stored=(storedclass==null ? new StoredClass[0] : new StoredClass[]{storedclass});
		return new DatabaseGraphIterator(this,stored);
	}

	public ObjectSet instances(String clazz) {
		return new MockObjectSet((Object[])instances.get(clazz));
	}
	
	public void verify() {
		assertFalse("database was not closed",opened);
	}
	
	public void add(StoredClass storedClass,Object[] instances) {
		storedClasses.put(storedClass.getName(),storedClass);
		this.instances.put(storedClass.getName(),instances);
	}
	
	public long getId(Object object) {
		return System.identityHashCode(object);
	}
}
