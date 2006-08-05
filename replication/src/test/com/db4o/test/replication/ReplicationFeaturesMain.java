/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.foundation.Collection4;
import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.Iterator4;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.ObjectState;
import com.db4o.replication.ReplicationConflictException;
import com.db4o.replication.ReplicationEvent;
import com.db4o.replication.ReplicationEventListener;
import com.db4o.replication.ReplicationProvider;
import com.db4o.replication.ReplicationSession;
import com.db4o.replication.db4o.Db4oReplicationProvider;

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
		while (i.hasNext()) {
			if (!contains(i.next())) return false;
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
		for(Iterator4 iter=iterator();iter.hasNext();) {
			if(!first) {
				buf.append(',');
			}
			else {
				first=false;
			}
			buf.append(iter.next().toString());
		}
		buf.append(']');
		return buf.toString();
	}
}

public class ReplicationFeaturesMain extends ReplicationTestCase {
	private static final String A = "A";
	private static final String B = "B";

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

		ObjectSet changes = origin.objectsChangedSinceLastReplication();
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
		String container = (String)_containersWithDeletedObjects.iterator().next();

		if (hasChanges(other(container))) return true;

		if (_direction.size() != 1) return false;
		return _direction.contains(container);
	}

	private boolean isDefaultReplicationBehaviorAllowed() {
		return _containerStateToPrevail != null && _containerStateToPrevail.isEmpty();
	}

	protected void actualTest() {
		_setA.add(A);
		_setB.add(B);

		_setBoth.addAll(_setA);
		_setBoth.addAll(_setB);

		_testCombination = 0;

		tstWithDeletedObjectsIn(_NONE);
//		tstWithDeletedObjectsIn(_setA);
//		tstWithDeletedObjectsIn(_setB);
//		tstWithDeletedObjectsIn(_setBoth);

		if (_intermittentErrors.length() > 0) {
			System.err.println("Intermittent errors found in test combinations:" + _intermittentErrors);
			ensure(false);
		}

	}

	protected void clean() {
		delete(new Class[]{Replicated.class});
	}

	private void changeObject(TestableReplicationProviderInside container, String name, String newName) {
		Replicated obj = find(container, name);
		if (obj == null) return;
		obj.setName(newName);
		container.update(obj);
		out("CHANGED: "+container+": "+name+" => "+newName+" - "+obj);
	}

	private void checkEmpty(TestableReplicationProviderInside provider) {
		if (provider.getStoredObjects(Replicated.class).hasNext())
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
			ensure(obj != null);
		} else {
			ensure(obj == null);
		}
	}

	private String containerName(TestableReplicationProviderInside container) {
		return container == _providerA ? "A" : "B";
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

	public void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
	}

	private TestableReplicationProviderInside container(String aOrB) {
		return aOrB.equals(A) ? _providerA : _providerB;
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

		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB, new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent e) {
				if (_containerStateToPrevail == null) {
					e.overrideWith(null);
					return;
				}

				if (_containerStateToPrevail.isEmpty()) return;  //Default replication behaviour.

				ObjectState override = _containerStateToPrevail.contains(A)
						? e.stateInProviderA()
						: e.stateInProviderB();
				e.overrideWith(override);
			}
		});

		if (_direction.size() == 1) {
			if (_direction.contains(A))	replication.setDirection(_providerB, _providerA);
			if (_direction.contains(B))	replication.setDirection(_providerA, _providerB);
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
//		printProviderContent(_providerA);
//		printProviderContent(_providerB);
	}
	
	private void printProviderContent(TestableReplicationProviderInside provider) {
		ObjectContainer db=((Db4oReplicationProvider)provider).objectContainer();
		ObjectSet result=db.query(Replicated.class);
		System.out.println("PROVIDER: "+provider);
		while(result.hasNext()) {
			System.out.println(result.next());
		}
	}

	private boolean tryToReplicate(final ReplicationSession replication) {

		try {
			replicate(replication, A);
			replicate(replication, B);
			ensure(!isReplicationConflictExceptionExpectedReplicatingModifications());
		} catch (ReplicationConflictException e) {
			out("Conflict exception during modification replication.");
			ensure(isReplicationConflictExceptionExpectedReplicatingModifications());
			return false;
		}

		try {
			if (isDeletionReplicationTriggered())
				replication.replicateDeletions(Replicated.class);

			ensure(!isReplicationConflictExceptionExpectedReplicatingDeletions());
		} catch (ReplicationConflictException e) {
			out("Conflict exception during deletion replication.");
			ensure(isReplicationConflictExceptionExpectedReplicatingDeletions());
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
		ObjectSet storedObjects = 
			container.getStoredObjects(Replicated.class);

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
		checkEmpty(_providerA);
		checkEmpty(_providerB);

		_providerA.storeNew(new Replicated("oldFromA"));
		_providerB.storeNew(new Replicated("oldFromB"));

		_providerA.commit();
		_providerB.commit();

		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB);

		replicateQueryingFrom(replication, _providerA, _providerB);
		replicateQueryingFrom(replication, _providerB, _providerA);

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
		return wasConflictWhileReplicatingModificationsQueryingFrom(A) || wasConflictWhileReplicatingModificationsQueryingFrom(B);
	}


	private boolean isModificationReplicationTriggered() {
		return wasModificationReplicationTriggeredQueryingFrom(A) || wasModificationReplicationTriggeredQueryingFrom(B);
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
		if (isChangedNameExpected(A, inspectedContainer)) return false;
		if (isChangedNameExpected(B, inspectedContainer)) return false;
		return true;
	}


	private String other(String aOrB) {
		return aOrB.equals(A) ? B : A;
	}

	private void performChanges() {
		if (_containersWithNewObjects.contains(A)) {
			_providerA.storeNew(new Replicated("newFromA"));
		}
		if (_containersWithNewObjects.contains(B)) {
			_providerB.storeNew(new Replicated("newFromB"));
		}

		if (hasDeletions(A)) {
			deleteObject(_providerA, "oldFromA");
			deleteObject(_providerA, "oldFromB");
		}
		if (hasDeletions(B)) {
			deleteObject(_providerB, "oldFromA");
			deleteObject(_providerB, "oldFromB");
		}

		if (hasChanges(A)) {
			changeObject(_providerA, "oldFromA", "oldFromAChangedInA");
			changeObject(_providerA, "oldFromB", "oldFromBChangedInA");
		}
		if (hasChanges(B)) {
			changeObject(_providerB, "oldFromA", "oldFromAChangedInB");
			changeObject(_providerB, "oldFromB", "oldFromBChangedInB");
		}

		_providerA.commit();
		_providerB.commit();
	}


	private String print(Set4 containerSet) {
		if (containerSet == null) return "null";
		if (containerSet.isEmpty()) return "NONE";
		if (containerSet.size() == 2) return "BOTH";
		return (String) containerSet.iterator().next();
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
		super.test();
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
