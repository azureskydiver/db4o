/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.other;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.db4ounit.DrsTestCase;

import db4ounit.Assert;


public class TheSimplest extends DrsTestCase {

	public void test() {
		storeInA();
		replicate();
		modifyInB();
		replicate2();
		modifyInA();
		replicate3();
	}

	private void replicate3() {
		replicateClass(a().provider(), b().provider(), SPCChild.class);

		ensureNames(a().provider(), "c3");
		ensureNames(b().provider(), "c3");
	}

	private void modifyInA() {
		SPCChild child = getTheObject(a().provider());

		child.setName("c3");

		a().provider().update(child);
		a().provider().commit();

		ensureNames(a().provider(), "c3");
	}

	private void replicate2() {
		replicateAll(b().provider(), a().provider());

		ensureNames(a().provider(), "c2");
		ensureNames(b().provider(), "c2");
	}

	private void storeInA() {
		SPCChild child = new SPCChild("c1");
		
		a().provider().storeNew(child);
		a().provider().commit();
		
		ensureNames(a().provider(), "c1");
	}
		
	private void replicate() {
		replicateAll(a().provider(), b().provider());

		ensureNames(a().provider(), "c1");
		ensureNames(b().provider(), "c1");
	}
	
	private void modifyInB() {
		SPCChild child = getTheObject(b().provider());

		child.setName("c2");
		b().provider().update(child);
		b().provider().commit();

		ensureNames(b().provider(), "c2");
	}
	
	private void ensureNames(TestableReplicationProviderInside provider, String childName) {
		ensureOneInstance(provider, SPCChild.class);
		SPCChild child = getTheObject(provider);
		Assert.areEqual(childName,child.getName());
	}

	protected void ensureOneInstance(TestableReplicationProviderInside provider, Class clazz) {
		ensureInstanceCount(provider, clazz, 1);
	}

	protected void ensureInstanceCount(TestableReplicationProviderInside provider, Class clazz, int count) {
		ObjectSet objectSet = provider.getStoredObjects(clazz);
		Assert.areEqual(count, objectSet.size());
	}

	private SPCChild getTheObject(TestableReplicationProviderInside provider) {
		return (SPCChild) getOneInstance(provider, SPCChild.class);
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