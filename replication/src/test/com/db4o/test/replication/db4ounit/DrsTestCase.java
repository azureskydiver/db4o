/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationSession;

import db4ounit.Assert;
import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public abstract class DrsTestCase implements TestCase, TestLifeCycle {
	
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

	protected void ensureOneInstance(TestableReplicationProviderInside provider, Class clazz) {
		ensureInstanceCount(provider, clazz, 1);
	}

	protected void ensureInstanceCount(TestableReplicationProviderInside provider, Class clazz, int count) {
		ObjectSet objectSet = provider.getStoredObjects(clazz);
		Assert.areEqual(count, objectSet.size());
	}

	protected Object getOneInstance(TestableReplicationProviderInside provider, Class clazz) {
		ObjectSet objectSet = provider.getStoredObjects(clazz);

		if (1 != objectSet.size())
			throw new RuntimeException("Found more than one instance of + " + clazz + " in provider = " + provider);

		return objectSet.next();
	}

	protected void replicateAll(TestableReplicationProviderInside providerFrom, TestableReplicationProviderInside providerTo) {
		//System.out.println("from = " + providerFrom + ", to = " + providerTo);
		ReplicationSession replication = Replication.begin(providerFrom, providerTo);
		ObjectSet allObjects = providerFrom.objectsChangedSinceLastReplication();

		if (!allObjects.hasNext())
			throw new RuntimeException("Can't find any objects to replicate");

		while (allObjects.hasNext()) {
			Object changed = allObjects.next();
			//System.out.println("changed = " + changed);
			replication.replicate(changed);
		}
		replication.commit();
	}

	protected void replicateClass(TestableReplicationProviderInside providerA, TestableReplicationProviderInside providerB, Class clazz) {
		//System.out.println("ReplicationTestcase.replicateClass");
		ReplicationSession replication = Replication.begin(providerA, providerB);
		ObjectSet allObjects = providerA.objectsChangedSinceLastReplication(clazz);
		while (allObjects.hasNext()) {
			final Object obj = allObjects.next();
			//System.out.println("obj = " + obj);
			replication.replicate(obj);
		}
		replication.commit();
	}

}
