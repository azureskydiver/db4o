/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.performance;

import com.db4o.ObjectSet;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.test.replication.ReplicationTestCase;

public class SimplePerformanceTests extends ReplicationTestCase {

	private static final int TOTAL_OBJECTS = 10000;
	private static final int CHANGED_OBJECTS = 100;

	protected void actualTest() {
		startTimer();
		storeObjects();
		replicateAll(_providerA, _providerB);
		logTime("Replicating " + TOTAL_OBJECTS + " new objects");
		modifyObjects(_providerB, "B");
		replicateAll(_providerB, _providerA);
		logTime("Replicating all from B to A");
		modifyObjects(_providerA, "A");
		replicateClass(_providerA, _providerB, SPTFlatObject.class);
		logTime("Replicating class from A to B");
	}

	protected void clean() {delete(new Class[]{SPTFlatObject.class});}

	private void storeObjects() {
		for (int i = 0; i < TOTAL_OBJECTS; i++) {
			_providerA.storeNew(new SPTFlatObject("flat " + i));
		}
		logTime("Storing " + TOTAL_OBJECTS + " flat objects to A");
		_providerA.commit();
		logTime("Commit A");
	}


	private void modifyObjects(TestableReplicationProviderInside provider, String name) {
		ObjectSet objectSet = provider.getStoredObjects(SPTFlatObject.class);
		for (int i = 0; i < CHANGED_OBJECTS; i++) {
			SPTFlatObject sptf = (SPTFlatObject) objectSet.next();
			sptf.setName("modified in " + name + i);
			provider.update(sptf);
		}
		logTime("Updating " + CHANGED_OBJECTS + " flat objects in " + name);
		provider.commit();
		logTime("Commit " + name);
	}

	public void test() {
		super.test();
	}


}
