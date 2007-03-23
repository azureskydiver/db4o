/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationEventListener;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.TestableReplicationProviderInside;

import db4ounit.Assert;
import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public abstract class DrsTestCase implements TestCase, TestLifeCycle {
	
	public static final Class[] mappings;
	public static final Class[] extraMappingsForCleaning = new Class[]{Map.class, List.class};

	static {
		mappings = new Class[]{
				Replicated.class,
				SPCParent.class, SPCChild.class,
				ListHolder.class, ListContent.class,
				MapContent.class,
				SimpleArrayContent.class, SimpleArrayHolder.class,
				R0.class, Pilot.class, Car.class, Student.class, Person.class};
	}
	

	private DrsFixture _a;
	private DrsFixture _b;
	
	public void setUp() throws Exception {
		cleanBoth();
		configure();
		openBoth();
		store();
		reopen();
	}

	private void cleanBoth() {
		_a.clean();
		_b.clean();
	}

	protected void clean() {
		for (int i = 0; i < mappings.length; i++) {
			a().provider().deleteAllInstances(mappings[i]);
			b().provider().deleteAllInstances(mappings[i]);
		}

		for (int i = 0; i < extraMappingsForCleaning.length; i++) {
			a().provider().deleteAllInstances(extraMappingsForCleaning[i]);
			b().provider().deleteAllInstances(extraMappingsForCleaning[i]);
		}

		a().provider().commit();
		b().provider().commit();
	}

	protected void store() {}
	
	protected void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
	}
	
	protected void reopen() throws Exception {
		closeBoth();
		openBoth();
	}

	private void openBoth() throws Exception {
		_a.open();
		_b.open();
	}
	
	public void tearDown() throws Exception {
		closeBoth();
		cleanBoth();
	}

	private void closeBoth() throws Exception {
		_a.close();
		_b.close();
	}
	
	public void a(DrsFixture fixture) {
		_a = fixture;
	}
	
	public void b(DrsFixture fixture) {
		_b = fixture;
	}
	
	public DrsFixture a() {
		return _a;
	}

	public DrsFixture b() {
		return _b;
	}

	protected void ensureOneInstance(DrsFixture fixture, Class clazz) {
		ensureInstanceCount(fixture, clazz, 1);
	}

	protected void ensureInstanceCount(DrsFixture fixture, Class clazz, int count) {
		ObjectSet objectSet = fixture.provider().getStoredObjects(clazz);
		Assert.areEqual(count, objectSet.size());
	}

	protected Object getOneInstance(DrsFixture fixture, Class clazz) {
		Iterator objectSet = fixture.provider().getStoredObjects(clazz).iterator();
		
		Object candidate = null;
		if (objectSet.hasNext()) {
			candidate = objectSet.next();
			
			if (objectSet.hasNext())
				 throw new RuntimeException("Found more than one instance of + " + clazz + " in provider = " + fixture);	 
		}
		
		return candidate;
	}

	protected void replicateAll(TestableReplicationProviderInside providerFrom, TestableReplicationProviderInside providerTo) {
		//System.out.println("from = " + providerFrom + ", to = " + providerTo);
		ReplicationSession replication = Replication.begin(providerFrom, providerTo);
		Iterator allObjects = providerFrom.objectsChangedSinceLastReplication().iterator();

		if (!allObjects.hasNext())
			throw new RuntimeException("Can't find any objects to replicate");

		while (allObjects.hasNext()) {
			Object changed = allObjects.next();
			//System.out.println("changed = " + changed);
			replication.replicate(changed);
		}
		replication.commit();
	}
	
	protected void replicateAll(
			TestableReplicationProviderInside from, TestableReplicationProviderInside to, ReplicationEventListener listener) {
		ReplicationSession replication = Replication.begin(from, to, listener);

		Iterator allObjects = from.objectsChangedSinceLastReplication().iterator();
		while (allObjects.hasNext()) {
			Object changed = allObjects.next();
			//System.out.println("changed = " + changed);
			replication.replicate(changed);
		}

		replication.commit();
	}

	protected void delete(Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			a().provider().deleteAllInstances(classes[i]);
			b().provider().deleteAllInstances(classes[i]);
		}
		
		a().provider().commit();
		b().provider().commit(); 
	}

	protected void replicateClass(TestableReplicationProviderInside providerA, TestableReplicationProviderInside providerB, Class clazz) {
		//System.out.println("ReplicationTestcase.replicateClass");
		ReplicationSession replication = Replication.begin(providerA, providerB);
		Iterator allObjects = providerA.objectsChangedSinceLastReplication(clazz).iterator();
		while (allObjects.hasNext()) {
			final Object obj = allObjects.next();
			//System.out.println("obj = " + obj);
			replication.replicate(obj);
		}
		replication.commit();
	}

	protected static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.toString());
		}
	}

}
