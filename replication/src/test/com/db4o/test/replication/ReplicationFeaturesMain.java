/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.ConflictResolver;
import com.db4o.replication.ReplicationProvider;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ReplicationFeaturesMain {

	private final Set _setA = new HashSet(1);
	private final Set _setB = new HashSet(1);
	private final Set _setBoth = new HashSet(2);
	private final Set _NONE = Collections.EMPTY_SET;

	protected TestableReplicationProvider _containerA;
	protected TestableReplicationProvider _containerB;

	private Set _direction;
	private Set _containersToQueryFrom;
	private Set _containersWithNewObjects;
	private Set _containersWithChangedObjects;
	private Set _objectsToPrevailInConflicts;

	private int _errors;
	private String _intermittentErrors = "";
	private int _testCombination;
	private static final String A = "A";
	private static final String B = "B";

	private int round = 0;

	public void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
	}

	public void test() {

		_setA.add(A);
		_setB.add(B);

		_setBoth.addAll(_setA);
		_setBoth.addAll(_setB);

		tstDirection(_setA);
		tstDirection(_setB);
		tstDirection(_setBoth);


		if (_intermittentErrors.length() > 0) {
			System.err.println("Intermittent errors found in test combinations:" + _intermittentErrors);
			Test.ensure(false);
		}

		System.out.println("=========================== TODO:");
		System.out.println("Peek for conflict");
		System.out.println("Run test on JDK1.");

		clean();
	}

	protected abstract void clean();

	private void tstDirection(Set direction) {
		_direction = direction;

		tstQueryingFrom(_setA);
		tstQueryingFrom(_setB);
		tstQueryingFrom(_setBoth);
	}

	private void tstQueryingFrom(Set containersToQueryFrom) {
		_containersToQueryFrom = containersToQueryFrom;

		tstWithNewObjectsIn(_NONE);
		tstWithNewObjectsIn(_setA);
		tstWithNewObjectsIn(_setB);
		tstWithNewObjectsIn(_setBoth);
	}

	private void tstWithNewObjectsIn(Set containersWithNewObjects) {
		_containersWithNewObjects = containersWithNewObjects;

		tstWithChangedObjectsIn(_NONE);
		tstWithChangedObjectsIn(_setA);
		tstWithChangedObjectsIn(_setB);
		tstWithChangedObjectsIn(_setBoth);
	}

	private void tstWithChangedObjectsIn(Set containers) {
		_containersWithChangedObjects = containers;

		tstWithObjectsPrevailingConflicts(_NONE);
		tstWithObjectsPrevailingConflicts(_setA);
		tstWithObjectsPrevailingConflicts(_setB);
	}


	private void tstWithObjectsPrevailingConflicts(Set containers) {
		_objectsToPrevailInConflicts = containers;

		_testCombination++;
//		System.out.println("" + _testCombination + "=================================");

		if (_testCombination < 0)
			return; //Use this to skip some combinations and avoid waiting.

		_errors = 0;
		while (true) {
			try {
				doIt();
				break;
			} catch (RuntimeException rx) {
				_errors++;
//                if (_errors == 10) {
				if (_errors == 1) {
					printCombination();
					sleep(100);
					throw rx;
				}
			}
		}
		if (_errors > 0)
			_intermittentErrors += "\n\t Combination: " + _testCombination + " (" + _errors + " errors)";
	}

	static private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void printCombination() {
		System.out.println("New Objects In: " + print(_containersWithNewObjects));
		System.out.println("Changed Objects In: " + print(_containersWithChangedObjects));
		System.out.println("Querying From: " + print(_containersToQueryFrom));
		System.out.println("Direction: To " + print(_direction));
		System.out.println("Prevailing Conflicts: " + print(_objectsToPrevailInConflicts));
	}

	private void doIt() {
		initState();
		//reopenContainers();


		final ReplicationSession replication = new GenericReplicationSession(_containerA, _containerB, new ConflictResolver() {

			public Object resolveConflict(ReplicationSession session, Object a, Object b) {
				if (_objectsToPrevailInConflicts.isEmpty()) return null;
				return _objectsToPrevailInConflicts.contains(A) ? a : b;
			}

		});

		performChanges();


		if (_direction.size() == 1) {
			if (_direction.contains(A)) {
				replication.setDirection(_containerB, _containerA);
			}
			if (_direction.contains(B)) {
				replication.setDirection(_containerA, _containerB);
			}
		}


		if (_containersToQueryFrom.contains(A)) {
			replicateQueryingFrom(replication, _containerA);
		}
		if (_containersToQueryFrom.contains(B)) {
			replicateQueryingFrom(replication, _containerB);
		}

		replication.commit();

		checkNames();

		//printRound();
	}

	private void printRound() {
		round++;

		if ((round % 10) == 0)
			System.out.println("round = " + round++);
	}


	private void checkNames() {
		checkNames(A, A);
		checkNames(A, B);
		checkNames(B, A);
		checkNames(B, B);
	}

	private void checkNames(String origin, String inspected) {
		checkName(container(inspected), "oldFrom" + origin, isOldNameExpected(inspected));
		checkName(container(inspected), "newFrom" + origin, isNewNameExpected(origin, inspected));
		checkName(container(inspected), "oldFromAChangedIn" + origin, isChangedNameExpected(origin, inspected));
		checkName(container(inspected), "oldFromBChangedIn" + origin, isChangedNameExpected(origin, inspected));
	}

	private TestableReplicationProvider container(String aOrB) {
		return aOrB.equals(A) ? _containerA : _containerB;
	}

	private boolean isOldNameExpected(String inspected) {
		if (isChangedNameExpected(A, inspected)) return false;
		if (isChangedNameExpected(B, inspected)) return false;
		return true;
	}

	private boolean isNewNameExpected(String origin, String inspected) {
		if (!_containersWithNewObjects.contains(origin)) return false;
		if (origin.equals(inspected)) return true;
		if (!_containersToQueryFrom.contains(origin)) return false;
		if (!_direction.contains(inspected)) return false;
		return true;
	}

	private boolean isChangedNameExpected(String changed, String inspected) {
		if (!hasChanges(changed)) return false;

		String other = other(inspected);
		if (prevailedInConflict(other))
			return isChangedNameExpected(changed, other);

		if (inspected.equals(changed)) return true;
		if (hasChanges(inspected)) return false;

		if (!_direction.contains(inspected)) return false;
		if (!wasReplicationTriggered()) return false;

		return true;
	}

	private boolean prevailedInConflict(String container) {
		if (!_objectsToPrevailInConflicts.contains(container)) return false;
		if (!_direction.contains(other(container))) return false;
		if (!wasReplicationTriggered()) return false;
		return true;
	}

	private boolean wasReplicationTriggered() {
		Set containersToTriggerReplication = new HashSet(_containersToQueryFrom);
		containersToTriggerReplication.retainAll(_containersWithChangedObjects);
		return !containersToTriggerReplication.isEmpty();
	}

	private boolean hasChanges(String container) {
		return _containersWithChangedObjects.contains(container);
	}

	private String other(String aOrB) {
		return aOrB.equals(A) ? B : A;
	}

	private void performChanges() {

		if (_containersWithNewObjects.contains(A)) {
			_containerA.storeNew(new Replicated("newFromA"));
		}
		if (_containersWithNewObjects.contains(B)) {
			_containerB.storeNew(new Replicated("newFromB"));
		}

		if (_containersWithChangedObjects.contains(A)) {
			changeObject(_containerA, "oldFromA", "oldFromAChangedInA");
			changeObject(_containerA, "oldFromB", "oldFromBChangedInA");
		}
		if (_containersWithChangedObjects.contains(B)) {
			changeObject(_containerB, "oldFromA", "oldFromAChangedInB");
			changeObject(_containerB, "oldFromB", "oldFromBChangedInB");
		}

	}

	private void changeObject(TestableReplicationProvider container, String name, String newName) {
		Replicated obj = find(container, name);
		obj.setName(newName);
		container.update(obj);
	}

	private void checkName(TestableReplicationProvider container, String name, boolean isExpected) {
//		System.out.println("");
//		System.out.println(name + (isExpected ? " " : " NOT ") + " expected in container " + containerName(container));
		Replicated obj = find(container, name);
//		System.out.println("obj = " + obj);
		if (isExpected) {
			ensure(obj != null);
		} else {
			ensure(obj == null);
		}
	}


	private String containerName(ReplicationProvider container) {
		if (container == _containerA) return A;
		if (container == _containerB) return B;
		throw new IllegalStateException();
	}

	private Replicated find(TestableReplicationProvider container, String name) {
		//System.out.println("container = " + container);
		//System.out.println("name = " + name);

		ObjectSet storedObjects = container.getStoredObjects(Replicated.class);

		int resultCount = 0;
		Replicated result = null;
		while (storedObjects.hasNext()) {
			Replicated replicated = (Replicated) storedObjects.next();
//			System.out.println("replicated = " + replicated);
			if (replicated == null)
				System.out.println("??????????????????????????????");
			if (name.equals(replicated.getName())) {
				result = replicated;
				resultCount++;
			}
		}

		if (resultCount > 1)
			fail("At most one object with name " + name + " was expected.");
		return result;

//		Query q = container.query();
//		q.constrain(Replicated.class);
//		q.descend("_name").constrain(name);
//		ObjectSet set = q.execute();
//		if (set.size() > 1) fail("At most one object with name " + name + " was expected.");
//		return (Replicated) set.next();
	}

	private static void fail(String string) {
		System.err.println(string);
		throw new RuntimeException(string);
	}

	protected abstract TestableReplicationProvider prepareProviderB();

	protected abstract TestableReplicationProvider prepareProviderA();

	private void initState() {
		_containerA = prepareProviderA();
		_containerB = prepareProviderB();

		_containerA.delete(Replicated.class);
		_containerB.delete(Replicated.class);

		checkEmpty(_containerA);
		checkEmpty(_containerB);

		final ReplicationSession replication = new GenericReplicationSession(_containerA, _containerB, new ConflictResolver() {
			public Object resolveConflict(ReplicationSession session, Object a, Object b) {
				return null;
			}
		});

		_containerA.storeNew(new Replicated("oldFromA"));
		_containerB.storeNew(new Replicated("oldFromB"));


		replicateQueryingFrom(replication, _containerA);
		replicateQueryingFrom(replication, _containerB);

		replication.commit();
	}

	private void checkEmpty(TestableReplicationProvider provider) {
		if (provider.getStoredObjects(Replicated.class).hasNext())
			throw new RuntimeException(provider.getName() + " is not empty");
	}

	private static void replicateQueryingFrom(ReplicationSession replication, ReplicationProvider origin) {
		ObjectSet it = origin.objectsChangedSinceLastReplication();
		while (it.hasNext()) replication.replicate(it.next());
	}

	private static void ensure(boolean condition) {
		if (!condition) throw new RuntimeException();
	}

	private String print(Set containerSet) {
		if (containerSet.isEmpty()) return "NONE";
		if (containerSet.size() == 2) return "BOTH";
		return (String) containerSet.iterator().next();
	}


}
