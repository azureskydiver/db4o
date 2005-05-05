/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;

public class ReplicationFeaturesMain {

	private final Set _A = new HashSet(1); 
	private final Set _B = new HashSet(1); 
	private final Set _BOTH = new HashSet(2); 
	private final Set _NONE = Collections.EMPTY_SET; 
	
    private ObjectContainer _containerA;
    private ObjectContainer _containerB;
	
	private Set _direction;
	private Set _containersToQueryFrom;
	private Set _containersWithNewObjects;
	private Set _containersWithChangedObjects;
	private Set _objectsToPrevailInConflicts;

    private int _errors;
    private String _intermittentErrors = "";
    private int _testCombination;
	
    
    public void configure(){
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
    }
	
    public void test() {
        
		reopenContainers();

		_A.add("A");
		_B.add("B");
		_BOTH.add("A");
		_BOTH.add("B");
		

//	WORK IN PROGRESS:	
        tstDirection(_BOTH);
	    tstDirection(_A);
	    tstDirection(_B);

        
        if (_intermittentErrors.length() > 0) { 
            System.err.println("Intermittent errors found in test combinations:" + _intermittentErrors);
            Test.ensure(false);
        }
	
		// TODO: replication.checkConflict(obj); //(peek)
    }

    private void reopenContainers() {
        _containerA = Test.reOpen();
        _containerB = Test.replica();
    }

	
	private void tstDirection(Set direction) {
		_direction = direction;
		
        tstQueryingFrom(_BOTH);
		tstQueryingFrom(_A);
		tstQueryingFrom(_B);
	}

	private void tstQueryingFrom(Set containers) {
		_containersToQueryFrom = containers;

        tstWithNewObjectsIn(_BOTH);
		tstWithNewObjectsIn(_A);
		tstWithNewObjectsIn(_B);
        tstWithNewObjectsIn(_NONE);
	}

	private void tstWithNewObjectsIn(Set containers) {
		_containersWithNewObjects = containers;
		
		System.out.print(".");
		
        tstWithChangedObjectsIn(_BOTH);
		tstWithChangedObjectsIn(_A);
		tstWithChangedObjectsIn(_B);
        tstWithChangedObjectsIn(_NONE);
	}

	private void tstWithChangedObjectsIn(Set containers) {
		_containersWithChangedObjects = containers;
		
		tstWithObjectsPrevailingConflicts(_A);
		tstWithObjectsPrevailingConflicts(_B);
        tstWithObjectsPrevailingConflicts(_NONE);
	}


	private void tstWithObjectsPrevailingConflicts(Set containers) {
		_objectsToPrevailInConflicts = containers;
		
        _testCombination++;
        if (_testCombination < 0) return; //Use this to skip some combinations and avoid waiting.

		_errors = 0;
        while (true) {
            try {
                doIt();
                break;
            } catch (RuntimeException rx) {
                _errors++;
                if (_errors == 10) {
                    printCombination();
                    throw rx;
                }
            }
        }
        if (_errors > 0) _intermittentErrors += "\n\t Combination: " + _testCombination + " (" + _errors +" errors)";
    }

    private void printCombination() {
        System.err.println("Direction: " + print(_direction));
        System.err.println("Querying From: " + print(_containersToQueryFrom));
        System.err.println("New Objects In: " + print(_containersWithNewObjects));
        System.err.println("Changed Objects In: " + print(_containersWithChangedObjects));
        System.err.println("Prevailing Conflicts: " + print(_objectsToPrevailInConflicts));
    }

    private void doIt() {
		initState();
		reopenContainers();
        
        performChanges();
        
		ReplicationProcess replication = _containerA.ext().replicationBegin(_containerB, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
                if (_objectsToPrevailInConflicts.isEmpty()) return null;
                return _objectsToPrevailInConflicts.contains("A") ? a  : b;
            }
        });

		if (_direction.size() == 1) {
			if (_direction.contains("A")) {replication.setDirection(_containerB, _containerA);}
			if (_direction.contains("B")) {replication.setDirection(_containerA, _containerB);}
		}

		
        if (_containersToQueryFrom.contains("A")) {
			replicateQueryingFrom(replication, _containerA);
        }
        if (_containersToQueryFrom.contains("B")) {
			replicateQueryingFrom(replication, _containerB);
        }
		
		replication.commit();
        
		checkNames();
	}

    private void checkNames() {
		checkNames("A", "A");
		checkNames("A", "B");
		checkNames("B", "A");
		checkNames("B", "B");
    }

	private void checkNames(String origin, String inspected) {
		checkName(container(inspected), "oldFrom" + origin, isOldNameExpected(inspected));
		checkName(container(inspected), "newFrom" + origin, isNewNameExpected(origin, inspected));
		checkName(container(inspected), "oldFromAChangedIn" + origin, isChangedNameExpected(origin, inspected));
		checkName(container(inspected), "oldFromBChangedIn" + origin, isChangedNameExpected(origin, inspected));
	}

    private ObjectContainer container(String aOrB) {
        return aOrB.equals("A") ? _containerA  : _containerB;
    }

    private boolean isOldNameExpected(String inspected) {
        if (isChangedNameExpected("A", inspected)) return false;
        if (isChangedNameExpected("B", inspected)) return false;
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
        if (prevailedInConflict(other)) return isChangedNameExpected(changed, other);

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
        return aOrB.equals("A") ? "B"  : "A";
    }

	private void performChanges() {

		if (_containersWithNewObjects.contains("A")) {
			_containerA.set(new Replicated("newFromA"));
        }
		if (_containersWithNewObjects.contains("B")) {
			_containerB.set(new Replicated("newFromB"));
        }
		
		if (_containersWithChangedObjects.contains("A")) {
            changeObject(_containerA, "oldFromA", "oldFromAChangedInA");
            changeObject(_containerA, "oldFromB", "oldFromBChangedInA");
        }
		if (_containersWithChangedObjects.contains("B")) {
            changeObject(_containerB, "oldFromA", "oldFromAChangedInB");
            changeObject(_containerB, "oldFromB", "oldFromBChangedInB");
        }
		
		_containerA.commit();
		_containerB.commit();
	}

    private void changeObject(ObjectContainer container, String name, String newName) {
        Replicated obj = find(container, name);
        obj._name = newName;
        container.set(obj);
    }

	private void checkName(ObjectContainer container, String name, boolean isExpected) {
        Replicated obj = find(container, name);
        if (isExpected) {
            ensure(obj != null);
        } else {
            ensure(obj == null);
        }
	}

    private Replicated find(ObjectContainer container, String name) {
        Query q = container.query();
        q.constrain(Replicated.class);
        q.descend("_name").constrain(name);
        ObjectSet set = q.execute();
        ensure(set.size() < 2);
        return (Replicated)set.next();
    }
    
	private void initState() {
		_containerA.commit();
		_containerB.commit();
		deleteAll(_containerA);
		deleteAll(_containerB);

		_containerA.set(new Replicated("oldFromA"));
		_containerB.set(new Replicated("oldFromB"));
		
		_containerA.commit();
		_containerB.commit();

		final ReplicationProcess replication = _containerA.ext().replicationBegin(_containerB, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });

        replicateQueryingFrom(replication, _containerA);
		replicateQueryingFrom(replication, _containerB);

        replication.commit();
	}

	private void deleteAll(ObjectContainer container) {
		ObjectSet all = container.get(null);
		while (all.hasNext()) {
			container.delete(all.next());
		}
		container.commit();
	}

    private static void replicateQueryingFrom(ReplicationProcess replication, ObjectContainer origin) {
        ObjectSet set = objectsToReplicate(replication, origin);
        while(set.hasNext()){
            Object next = set.next();
			replication.replicate(next);
        }
    }

	private static ObjectSet objectsToReplicate(ReplicationProcess replication, ObjectContainer origin) {
		Query q = origin.query();
		q.constrain(Replicated.class);
		replication.whereModified(q);
		return q.execute();
	}
    
    private static void checkAllEqual(ExtObjectContainer con1, ExtObjectContainer con2){
        DeepCompare comparator = new DeepCompare();
        
		Query q = con1.query();
		q.constrain(Replicated.class);
        ObjectSet all = q.execute();
        while(all.hasNext()){
            Object obj1 = all.next();
            con1.activate(obj1, Integer.MAX_VALUE);
            
            Db4oUUID uuid = con1.getObjectInfo(obj1).getUUID();
            Object obj2 = con2.getByUUID(uuid);
            con2.activate(obj2, Integer.MAX_VALUE);

            ensure(comparator.isEqual(obj1, obj2));
        }
    }

    private static void ensure(boolean condition) {
        if (!condition) throw new RuntimeException();
    }

    private String print(Set containerSet) {
        if (containerSet.isEmpty()) return "NONE";
        if (containerSet.size() == 2) return "BOTH";
		return (String)containerSet.iterator().next();
	}

	
}
