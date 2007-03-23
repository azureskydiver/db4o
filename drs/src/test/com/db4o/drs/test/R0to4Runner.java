/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.*;

import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.ReplicationReflector;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

import db4ounit.Assert;

public class R0to4Runner extends DrsTestCase {

//	 ------------------------------ FIELDS ------------------------------

	private static final int LINKERS = 4;

// --------------------------- CONSTRUCTORS ---------------------------

	public R0to4Runner() {
		super();
	}

	protected void clean() {
		delete(a().provider());
		delete(b().provider());
	}

	protected void delete(TestableReplicationProviderInside provider) {
		ArrayList toDelete = new ArrayList();

		Iterator rr = provider.getStoredObjects(R0.class).iterator();
		while (rr.hasNext()) {
			Object o = rr.next();

			ReflectClass claxx = ReplicationReflector.getInstance().reflector().forObject(o);
			setFieldsToNull(o, claxx);
			toDelete.add(o);
		}

		for (Iterator iterator = toDelete.iterator(); iterator.hasNext();) {
			Object o = iterator.next();

			//System.out.println("o = " + o);
			provider.delete(o);
		}

		provider.commit();
	}

	private void compareR4(TestableReplicationProviderInside a, TestableReplicationProviderInside b, boolean isSameExpected) {
		Iterator it = a.getStoredObjects(R4.class).iterator();
		while (it.hasNext()) {
			String name = ((R4) it.next()).name;

			Iterator it2 = b.getStoredObjects(R4.class).iterator();
			boolean found = false;
			while (it2.hasNext()) {
				String name2 = ((R4) it2.next()).name;
				if (name.equals(name2)) found = true;
			}
			Assert.isTrue(found == isSameExpected);
		}
	}

	private void copyAllToB(TestableReplicationProviderInside peerA, TestableReplicationProviderInside peerB) {
		Assert.isTrue(replicateAll(peerA, peerB, false) == LINKERS * 5);
	}

	private void ensureCount(TestableReplicationProviderInside provider, int linkers) {
		ensureCount(provider, R0.class, linkers * 5);
		ensureCount(provider, R1.class, linkers * 4);
		ensureCount(provider, R2.class, linkers * 3);
		ensureCount(provider, R3.class, linkers * 2);
		ensureCount(provider, R4.class, linkers * 1);
	}

	private void ensureCount(TestableReplicationProviderInside provider, Class clazz, int count) {
		Iterator instances = provider.getStoredObjects(clazz).iterator();
		int i = count;
		while (instances.hasNext()) {
			Object o = instances.next();
			i--;
		}
		Assert.isTrue(i == 0);
	}

	private void ensureR4Different(TestableReplicationProviderInside peerA, TestableReplicationProviderInside peerB) {
		compareR4(peerB, peerA, false);
	}

	private void ensureR4Same(TestableReplicationProviderInside peerA, TestableReplicationProviderInside peerB) {
		compareR4(peerB, peerA, true);
		compareR4(peerA, peerB, true);
	}

	private void init(TestableReplicationProviderInside peerA) {
		R0Linker lCircles = new R0Linker();
		lCircles.setNames("circles");
		lCircles.linkCircles();
		lCircles.store(peerA);

		R0Linker lList = new R0Linker();
		lList.setNames("list");
		lList.linkList();
		lList.store(peerA);

		R0Linker lThis = new R0Linker();
		lThis.setNames("this");
		lThis.linkThis();
		lThis.store(peerA);

		R0Linker lBack = new R0Linker();
		lBack.setNames("back");
		lBack.linkBack();
		lBack.store(peerA);

		peerA.commit();
	}

	private void modifyR4(TestableReplicationProviderInside provider) {
		Iterator it = provider.getStoredObjects(R4.class).iterator();
		while (it.hasNext()) {
			R4 r4 = (R4) it.next();
			r4.name = r4.name + "_";
			provider.update(r4);
		}
		provider.commit();
	}

	private int replicate(TestableReplicationProviderInside peerA, TestableReplicationProviderInside peerB) {
		return replicateAll(peerA, peerB, true);
	}

	private int replicateAll(TestableReplicationProviderInside peerA, TestableReplicationProviderInside peerB, boolean modifiedOnly) {
		ReplicationSession replication = Replication.begin(peerA, peerB);

		Iterator it = modifiedOnly
				? peerA.objectsChangedSinceLastReplication(R0.class).iterator()
				: peerA.getStoredObjects(R0.class).iterator();

		int replicated = 0;
		while (it.hasNext()) {
			R0 r0 = (R0) it.next();
			replication.replicate(r0);
			replicated++;
		}
		replication.commit();

		ensureCount(peerA, LINKERS);
		ensureCount(peerB, LINKERS);
		return replicated;
	}

	private void replicateNoneModified(TestableReplicationProviderInside peerA, TestableReplicationProviderInside peerB) {
		Assert.isTrue(replicate(peerA, peerB) == 0);
	}

	private void replicateR4(TestableReplicationProviderInside peerA, TestableReplicationProviderInside peerB) {
		int replicatedObjectsCount = replicateAll(peerA, peerB, true);
		Assert.isTrue(replicatedObjectsCount == LINKERS);
	}

	private void setFieldsToNull(Object object, ReflectClass claxx) {
		ReflectField[] fields;

		fields = claxx.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			ReflectField field = fields[i];
			if (field.isStatic()) continue;
			if (field.isTransient()) continue;
			field.setAccessible();
			field.set(object, null);
		}

		ReflectClass superclass = claxx.getSuperclass();
		if (superclass == null) return;
		setFieldsToNull(object, superclass);
	}

	public void test() {
		actualTest();
	}

	protected void actualTest() {

		init(a().provider());

		ensureCount(a().provider(), LINKERS);

		copyAllToB(a().provider(), b().provider());

		replicateNoneModified(a().provider(), b().provider());

		modifyR4(a().provider());

		ensureR4Different(a().provider(), b().provider());

		replicateR4(a().provider(), b().provider());

		ensureR4Same(a().provider(), b().provider());
	}

}
