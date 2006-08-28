/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationEventListener;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.collections.map.MapKey;
import com.db4o.test.replication.provider.Car;
import com.db4o.test.replication.provider.Pilot;
import com.db4o.test.replication.r0tor4.R0;

import java.util.Map;
import java.util.List;


public abstract class ReplicationTestCase {
	public static final Class[] mappings;
	public static final Class[] extraMappingsForCleaning = new Class[]{Map.class, List.class};

	static private final Collection4 PROVIDER_PAIRS = new Collection4();

	protected TestableReplicationProviderInside _providerA;
	protected TestableReplicationProviderInside _providerB;

	private long _timer;

	static {
		mappings = new Class[]{CollectionHolder.class, Replicated.class,
				SPCParent.class, SPCChild.class,
				ListHolder.class, ListContent.class,
				MapHolder.class, MapKey.class,
				SimpleArrayContent.class, SimpleArrayHolder.class,
				R0.class, Pilot.class, Car.class};
	}

	public static void registerProviderPair(TestableReplicationProviderInside providerA, TestableReplicationProviderInside providerB) {
		PROVIDER_PAIRS.add(new ProviderPair(providerA, providerB));
	}

	protected static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void actualTest();

	private void checkEmpty() {
		checkClean(_providerA);
		checkClean(_providerB);
	}

	protected void clean() {
		for (int i = 0; i < mappings.length; i++) {
			_providerA.deleteAllInstances(mappings[i]);
			_providerB.deleteAllInstances(mappings[i]);
		}

		for (int i = 0; i < extraMappingsForCleaning.length; i++) {
			_providerA.deleteAllInstances(extraMappingsForCleaning[i]);
			_providerB.deleteAllInstances(extraMappingsForCleaning[i]);
		}

		_providerA.commit();
		_providerB.commit();
	}

	protected void delete(Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			_providerA.deleteAllInstances(classes[i]);
			_providerB.deleteAllInstances(classes[i]);
		}
		_providerA.commit();
		_providerB.commit();
	}

	protected void destroy() {
		_providerA.destroy();
		_providerB.destroy();

		_providerA = null;
		_providerB = null;
	}
	
	protected static void ensure(boolean condition, String message) {
		if (!condition) throw new RuntimeException(message);
	}
	
	protected static void ensure(boolean condition) {
		ensure(condition, "");
	}
	
	protected static void ensureEquals(Object expected, Object actual) {
		if (expected.equals(actual)) return;
		ensure(false, "'" + expected + "' != '" + actual + "'");
	}

	protected void ensureInstanceCount(TestableReplicationProviderInside provider, Class clazz, int count) {
		ObjectSet objectSet = provider.getStoredObjects(clazz);
		ensureEquals(count, objectSet.size());
	}

	protected void ensureOneInstance(TestableReplicationProviderInside provider, Class clazz) {
		ensureInstanceCount(provider, clazz, 1);
	}

	protected Object getOneInstance(TestableReplicationProviderInside provider, Class clazz) {
		ObjectSet objectSet = provider.getStoredObjects(clazz);

		if (1 != objectSet.size())
			throw new RuntimeException("Found more than one instance of + " + clazz + " in provider = " + provider);

		return objectSet.next();
	}

	protected void logTime(String msg) {
		long time = System.currentTimeMillis();
		long duration = time - _timer;
		System.out.println(msg + " " + duration + "ms");
		_timer = System.currentTimeMillis();
	}

	public void printCombination(ProviderPair p) {
		String claxx = this.getClass().getName();
		String pa = p._providerA.getName();
		String pb = p._providerB.getName();

		String out = "Test = " + claxx + ", provider A = " + pa + ", provider B = " + pb;

		System.out.println(out);
	}

	protected void replicateAll(TestableReplicationProviderInside providerFrom, TestableReplicationProviderInside providerTo) {
		//System.out.println("from = " + providerFrom + ", to = " + providerTo);
		ReplicationSession replication = Replication.begin(providerFrom, providerTo);
		ObjectSet allObjects = providerFrom.objectsChangedSinceLastReplication();

		if (!allObjects.hasNext())
			throw new RuntimeException("Can't find any objects to replicate");

		while (allObjects.hasNext()) {
			Object changed = allObjects.next();
			System.out.println("changed = " + changed);
			replication.replicate(changed);
		}
		replication.commit();
	}

	protected void replicateAll(
			TestableReplicationProviderInside from, TestableReplicationProviderInside to, ReplicationEventListener listener) {
		ReplicationSession replication = Replication.begin(from, to, listener);

		ObjectSet allObjects = from.objectsChangedSinceLastReplication();
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

	protected void startTimer() {
		_timer = System.currentTimeMillis();
	}

	private void checkClean(TestableReplicationProviderInside p) {
		for (int i = 0; i < mappings.length; i++) {
			ObjectSet remains = p.getStoredObjects(mappings[i]);
			ensureEquals(0, remains.size());

			boolean notEmpty = false;
			while (remains.hasNext()) {
				notEmpty = true;
				System.out.println("remained = " + remains.next().getClass());
			}

			if (notEmpty) throw new RuntimeException(p + " is not cleaned");
		}
	}

	private void doActualTest() {
		try {
			actualTest();
		} catch (RuntimeException rx) {
			rx.printStackTrace();
			throw rx;
		} finally {
			clean();
			checkEmpty();
		}

		sleep(0);
	}

	private void prepareNextProviderPair(ProviderPair pair) {
		_providerA = pair._providerA;
		_providerB = pair._providerB;
		System.out.println("   Provider pair: " + _providerA + "  -  " + _providerB);
	}

	public void test() {
		Iterator4 it = PROVIDER_PAIRS.strictIterator();
		while (it.hasNext()) {
			prepareNextProviderPair((ProviderPair) it.next());
			doActualTest();
		}
	}
}
