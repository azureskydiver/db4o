/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.ObjectSet;
import com.db4o.drs.test.SPCChild;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationSession;

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


	private SPCChild getTheObject(TestableReplicationProviderInside provider) {
		return (SPCChild) getOneInstance(provider, SPCChild.class);
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