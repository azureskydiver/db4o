/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.Iterator;

import com.db4o.*;
import com.db4o.drs.*;
import com.db4o.drs.inside.*;
import com.db4o.foundation.*;

import db4ounit.Assert;

public class ReplicationFeaturesMain extends DrsTestCase {

	private static final String AStuff = "A";
	private static final String BStuff = "B";

	private final Set4 _setA = new Set4(1);
	private final Set4 _setB = new Set4(1);
	private final Set4 _setBoth = new Set4(2);
	private final Set4 _NONE = Set4.EMPTY_SET;

	private Set4 _direction;
	private Set4 _containersToQueryFrom;
	private Set4 _containersWithNewObjects;
	private Set4 _containersWithChangedObjects;
	private Set4 _containersWithDeletedObjects;
	private Set4 _containerStateToPrevail;

	private String _intermittentErrors = "";
	private int _testCombination;
	private static void fail(String string) {
		System.err.println(string);
		throw new RuntimeException(string);
	}

	private void replicateQueryingFrom(ReplicationSession replication, ReplicationProvider origin, ReplicationProvider other) {
		ReplicationConflictException exception = null;

		Iterator changes = origin.objectsChangedSinceLastReplication().iterator();
		while (changes.hasNext()) {
			Object changed = changes.next();
			try {
				replication.replicate(changed);
			} catch (ReplicationConflictException e) {
				exception = e;
			}
		}

		if (exception != null) throw exception;
	}

	private boolean isReplicationConflictExceptionExpectedReplicatingModifications() {
		return wasConflictReplicatingModifications() && isDefaultReplicationBehaviorAllowed();
	}

	private boolean isReplicationConflictExceptionExpectedReplicatingDeletions() {
		return wasConflictReplicatingDeletions() && isDefaultReplicationBehaviorAllowed();
	}

	private boolean wasConflictReplicatingDeletions() {
		if (_containersWithDeletedObjects.size() != 1) return false;
		String container = (String)firstContainerWithDeletedObjects();

		if (hasChanges(other(container))) return true;

		if (_direction.size() != 1) return false;
		return _direction.contains(container);
	}
	
	private String firstContainerWithDeletedObjects() {
		Iterator4 i = _containersWithDeletedObjects.iterator();
		i.moveNext();
		return (String)i.current();
	}

	private boolean isDefaultReplicationBehaviorAllowed() {
		return _containerStateToPrevail != null && _containerStateToPrevail.isEmpty();
	}

	protected void actualTest() {
		clean();
		_setA.add(AStuff);
		_setB.add(BStuff);

		_setBoth.addAll(_setA);
		_setBoth.addAll(_setB);

		_testCombination = 0;

		tstWithDeletedObjectsIn(_NONE);
		tstWithDeletedObjectsIn(_setA);
		tstWithDeletedObjectsIn(_setB);
		tstWithDeletedObjectsIn(_setBoth);

		if (_intermittentErrors.length() > 0) {
			System.err.println("Intermittent errors found in test combinations:" + _intermittentErrors);
			Assert.isTrue(false);
		}
	}

//	protected void clean() {
//		delete(new Class[]{Replicated.class});
//	}

	private void changeObject(TestableReplicationProviderInside container, String name, String newName) {
		Replicated obj = find(container, name);
		if (obj == null) return;
		obj.setName(newName);
		container.update(obj);
		out("CHANGED: "+container+": "+name+" => "+newName+" - "+obj);
	}

	private void checkEmpty(TestableReplicationProviderInside provider) {
		if (provider.getStoredObjects(Replicated.class).iterator().hasNext())
			throw new RuntimeException(provider.getName() + " is not empty");
	}

	private int checkNameCount=0;
	
	private void checkName(TestableReplicationProviderInside container, String name, boolean isExpected) {
		out("");
		out(name + (isExpected ? " " : " NOT") + " expected in container " + containerName(container));
		Replicated obj = find(container, name);
		out(String.valueOf(checkNameCount));
		checkNameCount++;
		if (isExpected) {
			Assert.isNotNull(obj);
		} else {
			Assert.isNull(obj);
		}
	}

	private String containerName(TestableReplicationProviderInside container) {
		return container == a().provider() ? "A" : "B";
	}

	private void checkNames() {
		checkNames(AStuff, AStuff);
		checkNames(AStuff, BStuff);
		checkNames(BStuff, AStuff);
		checkNames(BStuff, BStuff);
	}

	private void checkNames(String origin, String inspected) {
		checkName(container(inspected), "oldFrom" + origin, isOldNameExpected(inspected));
		checkName(container(inspected), "newFrom" + origin, isNewNameExpected(origin, inspected));
		checkName(container(inspected), "oldFromAChangedIn" + origin, isChangedNameExpected(origin, inspected));
		checkName(container(inspected), "oldFromBChangedIn" + origin, isChangedNameExpected(origin, inspected));
	}

//	public void configure() {
//		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
//		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
//	}

	private TestableReplicationProviderInside container(String aOrB) {
		return aOrB.equals(AStuff) ? a().provider() : b().provider();
	}

	private void deleteObject(TestableReplicationProviderInside container, String name) {
		Replicated obj = find(container, name);
		container.delete(obj);
	}

	private void doIt() {
		initState();

		printProvidersContent("before changes");
		
		performChanges();

		printProvidersContent("after changes");

		final ReplicationSession replication = new GenericReplicationSession(a().provider(), b().provider(), new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent e) {
				if (_containerStateToPrevail == null) {
					e.overrideWith(null);
					return;
				}

				if (_containerStateToPrevail.isEmpty()) return;  //Default replication behaviour.

				ObjectState override = _containerStateToPrevail.contains(AStuff)
						? e.stateInProviderA()
						: e.stateInProviderB();
				e.overrideWith(override);
			}
		});

		if (_direction.size() == 1) {
			if (_direction.contains(AStuff))	replication.setDirection(b().provider(), a().provider());
			if (_direction.contains(BStuff))	replication.setDirection(a().provider(), b().provider());
		}
		out("DIRECTION: "+_direction);
		boolean successful = tryToReplicate(replication);

		replication.commit();

		printProvidersContent("after replication");
		
		if (successful)
			checkNames();

		clean();
		
	}

	private void printProvidersContent(String msg) {
//		System.out.println("*** "+msg);
//		printProviderContent(a().provider());
//		printProviderContent(b().provider());
	}
	
//	private void printProviderContent(TestableReplicationProviderInside provider) {
//		ObjectContainer db=((Db4oReplicationProvider)provider).objectContainer();
//		ObjectSet result=db.query(Replicated.class);
//		System.out.println("PROVIDER: "+provider);
//		while(result.hasNext()) {
//			System.out.println(result.next());
//		}
//	}

	private boolean tryToReplicate(final ReplicationSession replication) {

		try {
			replicate(replication, AStuff);
			replicate(replication, BStuff);
			Assert.isFalse(isReplicationConflictExceptionExpectedReplicatingModifications());
		} catch (ReplicationConflictException e) {
			out("Conflict exception during modification replication.");
			Assert.isTrue(isReplicationConflictExceptionExpectedReplicatingModifications());
			return false;
		}

		try {
			if (isDeletionReplicationTriggered())
				replication.replicateDeletions(Replicated.class);

			Assert.isFalse(isReplicationConflictExceptionExpectedReplicatingDeletions());
		} catch (ReplicationConflictException e) {
			out("Conflict exception during deletion replication.");
			Assert.isTrue(isReplicationConflictExceptionExpectedReplicatingDeletions());
			return false;
		}

		return true;
	}

	private void replicate(final ReplicationSession replication, String originName) {
		ReplicationProvider origin = container(originName);
		ReplicationProvider destination = container(other(originName));

		if (!_containersToQueryFrom.contains(originName)) return;

		replicateQueryingFrom(replication, origin, destination);
	}

	private Replicated find(TestableReplicationProviderInside container, String name) {
		Iterator storedObjects = container.getStoredObjects(Replicated.class).iterator();

		int resultCount = 0;
		Replicated result = null;
		while (storedObjects.hasNext()) {
			Replicated replicated = (Replicated) storedObjects.next();
			if (replicated == null)
				throw new RuntimeException();
			if (name.equals(replicated.getName())) {
				result = replicated;
				resultCount++;
			}
		}

		if (resultCount > 1)
			fail("At most one object with name " + name + " was expected.");
		return result;
	}

	private boolean hasChanges(String container) {
		return _containersWithChangedObjects.contains(container);
	}

	private boolean hasDeletions(String container) {
		return _containersWithDeletedObjects.contains(container);
	}

	private void initState() {
		checkEmpty(a().provider());
		checkEmpty(b().provider());

		a().provider().storeNew(new Replicated("oldFromA"));
		b().provider().storeNew(new Replicated("oldFromB"));

		a().provider().commit();
		b().provider().commit();

		final ReplicationSession replication = new GenericReplicationSession(a().provider(), b().provider());

		replicateQueryingFrom(replication, a().provider(), b().provider());
		replicateQueryingFrom(replication, b().provider(), a().provider());

		replication.commit();
	}


	private boolean isChangedNameExpected(String changedContainer, String inspectedContainer) {
		if (!hasChanges(changedContainer)) return false;
		if (isDeletionExpected(inspectedContainer)) return false;
		if (isDeletionExpected(changedContainer)) return false;

		if (inspectedContainer.equals(changedContainer))
			return !didReceiveRemoteState(inspectedContainer);

		return didReceiveRemoteState(inspectedContainer);
	}


	private boolean didReceiveRemoteState(String inspectedContainer) {
		String other = other(inspectedContainer);

		if (isDirectionTo(other)) return false;

		if (_containerStateToPrevail == null) return false;

		if (_containerStateToPrevail.contains(inspectedContainer)) return false;

		if (_containerStateToPrevail.contains(other)) {
			if (isModificationReplicationTriggered()) return true;
			if (isDeletionReplicationTriggered()) return true;
			return false;
		}

		//No override to prevail. Default replication behavior.

		if (hasChanges(inspectedContainer)) return false; //A conflict would have been ignored long ago.

		return isModificationReplicationTriggered();
	}


	private boolean isDeletionReplicationTriggered() {
		return !_containersWithDeletedObjects.isEmpty();
	}

	private boolean isDirectionTo(String container) {
		return _direction.size() == 1 && _direction.contains(container);
	}

	private boolean wasConflictReplicatingModifications() {
		return wasConflictWhileReplicatingModificationsQueryingFrom(AStuff) || wasConflictWhileReplicatingModificationsQueryingFrom(BStuff);
	}


	private boolean isModificationReplicationTriggered() {
		return wasModificationReplicationTriggeredQueryingFrom(AStuff) || wasModificationReplicationTriggeredQueryingFrom(BStuff);
	}

	private boolean isDeletionExpected(String inspectedContainer) {
		if (_containerStateToPrevail == null)
			return hasDeletions(inspectedContainer);

		if (_containerStateToPrevail.contains(inspectedContainer))
			return hasDeletions(inspectedContainer);

		String other = other(inspectedContainer);
		if (isDirectionTo(other)) return hasDeletions(inspectedContainer);

		if (_containerStateToPrevail.contains(other))
			return hasDeletions(other);

		//_containerStateToPrevail is empty (default replication behaviour)
		return isDeletionReplicationTriggered();
	}

	private boolean isNewNameExpected(String origin, String inspected) {
		if (!_containersWithNewObjects.contains(origin)) return false;
		if (origin.equals(inspected)) return true;

		if (_containerStateToPrevail == null) return false;
		if (_containerStateToPrevail.contains(inspected)) return false;

		if (!_containersToQueryFrom.contains(origin)) return false;
		return _direction.contains(inspected);
	}

	private boolean isOldNameExpected(String inspectedContainer) {
		if (isDeletionExpected(inspectedContainer)) return false;
		if (isChangedNameExpected(AStuff, inspectedContainer)) return false;
		if (isChangedNameExpected(BStuff, inspectedContainer)) return false;
		return true;
	}


	private String other(String aOrB) {
		return aOrB.equals(AStuff) ? BStuff : AStuff;
	}

	private void performChanges() {
		if (_containersWithNewObjects.contains(AStuff)) {
			a().provider().storeNew(new Replicated("newFromA"));
		}
		if (_containersWithNewObjects.contains(BStuff)) {
			b().provider().storeNew(new Replicated("newFromB"));
		}

		if (hasDeletions(AStuff)) {
			deleteObject(a().provider(), "oldFromA");
			deleteObject(a().provider(), "oldFromB");
		}
		if (hasDeletions(BStuff)) {
			deleteObject(b().provider(), "oldFromA");
			deleteObject(b().provider(), "oldFromB");
		}

		if (hasChanges(AStuff)) {
			changeObject(a().provider(), "oldFromA", "oldFromAChangedInA");
			changeObject(a().provider(), "oldFromB", "oldFromBChangedInA");
		}
		if (hasChanges(BStuff)) {
			changeObject(b().provider(), "oldFromA", "oldFromAChangedInB");
			changeObject(b().provider(), "oldFromB", "oldFromBChangedInB");
		}

		a().provider().commit();
		b().provider().commit();
	}


	private String print(Set4 containerSet) {
		if (containerSet == null) return "null";
		if (containerSet.isEmpty()) return "NONE";
		if (containerSet.size() == 2) return "BOTH";
		return first(containerSet);
	}
	
	private String first(Set4 containerSet) {
		Iterator4 i = containerSet.iterator();
		i.moveNext();
		return (String) i.current();
	}

	private void printCombination() {
		out("" + _testCombination + " =================================");
		out("New Objects In: " + print(_containersWithNewObjects));
		out("Changed Objects In: " + print(_containersWithChangedObjects));
		out("Deleted Objects In: " + print(_containersWithDeletedObjects));
		out("Querying From: " + print(_containersToQueryFrom));
		out("Direction: To " + print(_direction));
		out("Prevailing State: " + print(_containerStateToPrevail));
	}

	private void runCurrentCombination() {
		_testCombination++;
		out("" + _testCombination + " =================================");
		printCombination();

		if (_testCombination < 0)  //Use this when debugging to skip some combinations and avoid waiting.
			return;

		int _errors = 0;
		while (true) {
			try {
				doIt();
				break;
			} catch (RuntimeException rx) {
				_errors++;
				if (_errors == 1) {
					sleep(100);
					printCombination();
					throw rx;
				}
			}
		}
		if (_errors > 0)
			_intermittentErrors += "\n\t Combination: " + _testCombination + " (" + _errors + " errors)";
	}

	private static void out(String string) {
		//System.out.println(string);
	}

	public void test() {
		actualTest();
	}

	private void tstDirection(Set4 direction) {
		_direction = direction;

		tstQueryingFrom(_setA);
		tstQueryingFrom(_setB);
		tstQueryingFrom(_setBoth);
	}

	private void tstQueryingFrom(Set4 containersToQueryFrom) {
		_containersToQueryFrom = containersToQueryFrom;

		tstWithNewObjectsIn(_NONE);
		tstWithNewObjectsIn(_setA);
		tstWithNewObjectsIn(_setB);
		tstWithNewObjectsIn(_setBoth);
	}

	private void tstWithChangedObjectsIn(Set4 containers) {
		_containersWithChangedObjects = containers;

		tstWithContainerStateToPrevail(_NONE);
		tstWithContainerStateToPrevail(_setA);
		tstWithContainerStateToPrevail(_setB);
		tstWithContainerStateToPrevail(null);
	}

	private void tstWithDeletedObjectsIn(Set4 containers) {
		_containersWithDeletedObjects = containers;

		tstDirection(_setA);
		tstDirection(_setB);
		tstDirection(_setBoth);
	}

	private void tstWithNewObjectsIn(Set4 containersWithNewObjects) {
		_containersWithNewObjects = containersWithNewObjects;

		tstWithChangedObjectsIn(_NONE);
		tstWithChangedObjectsIn(_setA);
		tstWithChangedObjectsIn(_setB);
		tstWithChangedObjectsIn(_setBoth);
	}

	private void tstWithContainerStateToPrevail(Set4 containers) {
		_containerStateToPrevail = containers;

		runCurrentCombination();
	}

	private boolean wasConflictWhileReplicatingModificationsQueryingFrom(String container) {
		if (!wasModificationReplicationTriggeredQueryingFrom(container)) return false;
		if (_containersWithChangedObjects.containsAll(_direction)) return true;
		return hasDeletions(other(container));
	}

	private boolean wasModificationReplicationTriggeredQueryingFrom(String container) {
		if (!_containersToQueryFrom.contains(container)) return false;
		if (_containersWithDeletedObjects.contains(container)) return false;
		return _containersWithChangedObjects.contains(container);
	}	
	
}

class Set4 {
	public static final Set4 EMPTY_SET = new Set4(0);
	
	Hashtable4 _table;
	
	public Set4(int size) {
		_table = new Hashtable4(size);
	}
	
	public void add(Object element) {
		_table.put(element, element);
	}
	
	public void addAll(Set4 other) {
		other._table.forEachKey(new Visitor4() {
			public void visit(Object element) {
				add(element);
			}
		});
	}
	
	public boolean isEmpty() {
		return _table.size() == 0;
	}
	
	public int size() {
		return _table.size();
	}
	
	public boolean contains(Object element) {
		return _table.get(element) != null;
	}
	
	public boolean containsAll(Set4 other) {
		Iterator4 i = other.iterator();
		while (i.moveNext()) {
			if (!contains(i.current())) return false;
		}
		return true;
	}
	
	public Iterator4 iterator() {
		final Collection4 elements = new Collection4();
		_table.forEachKey(new Visitor4() {
			public void visit(Object element) {
				elements.add(element);
			}
		});
		return elements.iterator();
	}
	
	public String toString() {
		StringBuffer buf=new StringBuffer("[");
		boolean first=true;
		for(Iterator4 iter=iterator();iter.moveNext();) {
			if(!first) {
				buf.append(',');
			}
			else {
				first=false;
			}
			buf.append(iter.current().toString());
		}
		buf.append(']');
		return buf.toString();
	}
}