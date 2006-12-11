/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.Iterator;

import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.drs.test.SPCChild;

import db4ounit.ArrayAssert;
import db4ounit.Assert;


public class ByteArrayTest extends DrsTestCase {

	public void test() {
		storeInA();
		replicate();
//		modifyInB();
//		replicate2();
//		modifyInA();
//		replicate3();
	}

//	private void replicate3() {
//		replicateClass(a().provider(), b().provider(), SPCChild.class);
//
//		ensureNames(a().provider(), "c3");
//		ensureNames(b().provider(), "c3");
//	}

//	private void modifyInA() {
//		SPCChild child = getTheObject(a().provider());
//
//		child.setName("c3");
//
//		a().provider().update(child);
//		a().provider().commit();
//
//		ensureNames(a().provider(), "c3");
//	}

//	private void replicate2() {
//		replicateAll(b().provider(), a().provider());
//
//		ensureNames(a().provider(), "c2");
//		ensureNames(b().provider(), "c2");
//	}

	private void storeInA() {
		ByteArrayContainer container = new ByteArrayContainer();
		container.byteArray = new byte[]{1,2,3};
		
		a().provider().storeNew(container);
		a().provider().commit();
		
		ensureNames(a().provider(), new byte[]{1,2,3});
	}
		
	private void replicate() {
		replicateAll(a().provider(), b().provider());

		ensureNames(a().provider(), new byte[]{1,2,3});
		ensureNames(b().provider(), new byte[]{1,2,3});
	}
	
//	private void modifyInB() {
//		SPCChild child = getTheObject(b().provider());
//
//		child.setName("c2");
//		b().provider().update(child);
//		b().provider().commit();
//
//		ensureNames(b().provider(), "c2");
//	}
	
	private void ensureNames(TestableReplicationProviderInside provider, byte[] bs) {
		ensureOneInstance(provider, ByteArrayContainer.class);
		ByteArrayContainer c = getTheObject(provider);
		ArrayAssert.areEqual(c.byteArray, bs);
	}


	private ByteArrayContainer getTheObject(TestableReplicationProviderInside provider) {
		return (ByteArrayContainer) getOneInstance(provider, ByteArrayContainer.class);
	}

	protected void replicateClassasadsa(TestableReplicationProviderInside providerA, TestableReplicationProviderInside providerB, Class clazz) {
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