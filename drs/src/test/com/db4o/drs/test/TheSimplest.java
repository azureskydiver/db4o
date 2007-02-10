/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.Iterator;

import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.drs.test.SPCChild;

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

		ensureNames(a(), "c3");
		ensureNames(b(), "c3");
	}

	private void modifyInA() {
		SPCChild child = getTheObject(a());

		child.setName("c3");

		a().provider().update(child);
		a().provider().commit();

		ensureNames(a(), "c3");
	}

	private void replicate2() {
		replicateAll(b().provider(), a().provider());

		ensureNames(a(), "c2");
		ensureNames(b(), "c2");
	}

	private void storeInA() {
		SPCChild child = new SPCChild("c1");
		
		a().provider().storeNew(child);
		a().provider().commit();
		
		ensureNames(a(), "c1");
	}
		
	private void replicate() {
		replicateAll(a().provider(), b().provider());

		ensureNames(a(), "c1");
		ensureNames(b(), "c1");
	}
	
	private void modifyInB() {
		SPCChild child = getTheObject(b());

		child.setName("c2");
		b().provider().update(child);
		b().provider().commit();

		ensureNames(b(), "c2");
	}
	
	private void ensureNames(DrsFixture fixture, String childName) {
		ensureOneInstance(fixture, SPCChild.class);
		SPCChild child = getTheObject(fixture);
		Assert.areEqual(childName,child.getName());
	}


	private SPCChild getTheObject(DrsFixture fixture) {
		return (SPCChild) getOneInstance(fixture, SPCChild.class);
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

}