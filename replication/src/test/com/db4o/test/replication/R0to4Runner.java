/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.ConflictResolver;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;

public abstract class R0to4Runner {
	protected TestableReplicationProvider peerA;
	protected TestableReplicationProvider peerB;

	protected abstract TestableReplicationProvider prepareProviderA();

	protected abstract TestableReplicationProvider prepareProviderB();

	private final static ConflictResolver _ignoreConflictHandler = new ConflictResolver() {
		public Object resolveConflict(ReplicationSession ignored, Object a, Object b) {
			return null;
		}
	};

	private static final int LINKERS = 4;

	public void test() {

		peerA = prepareProviderA();
		peerB = prepareProviderB();

		delete(new Class[]{R0.class});

		init(peerA, peerB);
		ensureCount(peerA, LINKERS);

		copyAllToB(peerA, peerB);
		replicateNoneModified(peerA, peerB);

		modifyR4(peerA);

		ensureR4Different(peerA, peerB);

		replicateR4(peerA, peerB);

		ensureR4Same(peerA, peerB);
	}

	private void delete(Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			peerA.delete(classes[i]);
			peerB.delete(classes[i]);
		}
		peerA.commit();
		peerB.commit();
	}

	private void init(TestableReplicationProvider peerA, TestableReplicationProvider peerB) {

		ReplicationSession replication = Replication.begin(peerA, peerB, _ignoreConflictHandler);

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

		replication.commit();

	}

	private void ensureR4Different(TestableReplicationProvider peerA, TestableReplicationProvider peerB) {
		compareR4(peerB, peerA, false);
	}

	private void ensureR4Same(TestableReplicationProvider peerA, TestableReplicationProvider peerB) {
		compareR4(peerB, peerA, true);
		compareR4(peerA, peerB, true);
	}

	private void compareR4(TestableReplicationProvider a, TestableReplicationProvider b, boolean isSameExpected) {
		ObjectSet it = a.getStoredObjects(R4.class);
		while (it.hasNext()) {
			String name = ((R4) it.next()).name;

			ObjectSet it2 = b.getStoredObjects(R4.class);
			boolean found = false;
			while (it2.hasNext()) {
				String name2 = ((R4) it2.next()).name;
				if (name.equals(name2)) found = true;
			}
			Test.ensure(found == isSameExpected);
		}
	}

	private void modifyR4(TestableReplicationProvider provider) {
		ObjectSet it = provider.getStoredObjects(R4.class);
		while (it.hasNext()) {
			R4 r4 = (R4) it.next();
			r4.name = r4.name + "_";
			provider.update(r4);
		}
	}

	private void copyAllToB(TestableReplicationProvider peerA, TestableReplicationProvider peerB) {
		Test.ensure(replicateAll(peerA, peerB, false) == LINKERS * 5);
	}

	private void replicateNoneModified(TestableReplicationProvider peerA, TestableReplicationProvider peerB) {
		Test.ensure(replicateAll(peerA, peerB) == 0);
	}

	private int replicateAll(TestableReplicationProvider peerA, TestableReplicationProvider peerB) {
		return replicateAll(peerA, peerB, true);
	}

	private void replicateR4(TestableReplicationProvider peerA, TestableReplicationProvider peerB) {
		int replicatedObjectsCount = replicateAll(peerA, peerB, true);
		Test.ensure(replicatedObjectsCount == LINKERS);
	}

	private int replicateAll(TestableReplicationProvider peerA, TestableReplicationProvider peerB, boolean modifiedOnly) {

		ReplicationSession replication = Replication.begin(peerA, peerB, _ignoreConflictHandler);

		ObjectSet it = modifiedOnly
				? peerA.objectsChangedSinceLastReplication(R0.class)
				: peerA.getStoredObjects(R0.class);

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

	private void ensureCount(TestableReplicationProvider provider, int linkers) {
		ensureCount(provider, R0.class, linkers * 5);
		ensureCount(provider, R1.class, linkers * 4);
		ensureCount(provider, R2.class, linkers * 3);
		ensureCount(provider, R3.class, linkers * 2);
		ensureCount(provider, R4.class, linkers * 1);
	}

	private void ensureCount(TestableReplicationProvider provider, Class clazz, int count) {
		ObjectSet instances = provider.getStoredObjects(clazz);
		int i = count;
		while (instances.hasNext()) {
			instances.next();
			i--;
		}
		Test.ensure(i == 0);
	}

}
