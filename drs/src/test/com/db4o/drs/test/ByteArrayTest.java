/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.Iterator;

import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.TestableReplicationProviderInside;

import db4ounit.ArrayAssert;

/**
 * Design of this case is copied from com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.
 */
public class ByteArrayTest extends DrsTestCase {
	static final int ARRAY_LENGTH = 5;
	
	static byte[] expected = createByteArray(); 
	
	public void test() {
		storeInA();
		replicate();
		modifyInB();
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
		IByteArrayHolder byteArrayHolder = new ByteArrayHolder(createByteArray());
		
		a().provider().storeNew(byteArrayHolder);
		a().provider().commit();
		
		ensureNames(a(), expected);
	}
		
	private void replicate() {
		replicateAll(a().provider(), b().provider());

		ensureNames(a(), expected);
		ensureNames(b(), expected);
	}
	
	private void modifyInB() {
//		ByteArrayHolder c = getTheObject(b());
//
//		child.setName("c2");
//		b().provider().update(child);
//		b().provider().commit();
//
//		ensureNames(b().provider(), "c2");
	}
	
	private void ensureNames(DrsFixture fixture, byte[] bs) {
		ensureOneInstance(fixture, IByteArrayHolder.class);
		IByteArrayHolder c = getTheObject(fixture);
		ArrayAssert.areEqual(c.getBytes(), bs);
	}


	private IByteArrayHolder getTheObject(DrsFixture fixture) {
		return (IByteArrayHolder) getOneInstance(fixture, IByteArrayHolder.class);
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
	
	static byte[] createByteArray() {
		byte[] bytes = new byte[ARRAY_LENGTH];
		for (byte i=0; i<bytes.length; ++i) {
			bytes[i] = i;
		}
		return bytes;
	}

}