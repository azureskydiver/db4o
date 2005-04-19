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
    private String _intermittence = "Intermittence at:";
    private int _counter;
	
    
    public void configure(){
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
    }
	
    public void test() {
        
		_containerA = Test.objectContainer();
		_containerB = Test.replica();

		_A.add(_containerA);
		_B.add(_containerB);
		_BOTH.add(_containerA);
		_BOTH.add(_containerB);
		

		produceBug();

/*        
//	WORK IN PROGRESS:	
		tstDirection(_A);
		tstDirection(_B);
		tstDirection(_BOTH);
*/
		
//      TODO: replication.checkConflict(obj); //(peek)
		
    }

	
	private void produceBug() {
		initState();
		System.err.println("-------------initState() Done");
		Test.ensure(find(_containerA, "oldFromA") != null);
		Test.ensure(find(_containerB, "oldFromA") != null);

        changeObject(_containerB, "oldFromA", "oldFromAChangedInB");
		_containerB.commit();
		Test.ensure(find(_containerB, "oldFromA") == null);
        
		ReplicationProcess replication = _containerA.ext().replicationBegin(_containerB, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });
		System.err.println("-------------Starting to replicate");
		replicateQueryingFrom(replication, _containerB);
		replication.commit();
		System.err.println("-------------Replication Done");
		
		Test.ensure(find(_containerA, "oldFromA") == null);
		Test.ensure(find(_containerA, "oldFromAChangedInB") != null);
	}

	
	private void tstDirection(Set direction) {
		_direction = direction;
		
		tstQueryingFrom(_A);
		tstQueryingFrom(_B);
		tstQueryingFrom(_BOTH);
	}

	private void tstQueryingFrom(Set containers) {
		_containersToQueryFrom = containers;
		
		tstWithNewObjectsIn(_NONE);
		tstWithNewObjectsIn(_A);
		tstWithNewObjectsIn(_B);
		tstWithNewObjectsIn(_BOTH);
	}

	private void tstWithNewObjectsIn(Set containers) {
		_containersWithNewObjects = containers;
		
		tstWithChangedObjectsIn(_NONE);
		tstWithChangedObjectsIn(_A);
		tstWithChangedObjectsIn(_B);
		tstWithChangedObjectsIn(_BOTH);
	}

	private void tstWithChangedObjectsIn(Set containers) {
		_containersWithChangedObjects = containers;
		
		tstWithObjectsPrevailingConflicts(_NONE);
		tstWithObjectsPrevailingConflicts(_A);
		tstWithObjectsPrevailingConflicts(_B);
	}


	private void tstWithObjectsPrevailingConflicts(Set containers) {
		_objectsToPrevailInConflicts = containers;
		
        _counter++;
        if (_counter < 0) return;

		_errors = 0;
        tryToDoIt();
        tryToDoIt();
        if (_errors == 2) {
			System.err.println("Direction: " + print(_direction));
			System.err.println("Querying From: " + print(_containersToQueryFrom));
			System.err.println("New Objects In: " + print(_containersWithNewObjects));
			System.err.println("Changed Objects In: " + print(_containersWithChangedObjects));
			//System.err.println("Prevailing Conflicts: " + print(_objectsToPrevailInConflicts));

            System.err.println(_intermittence);

			throw new RuntimeException();
        }
        if (_errors > 0) _intermittence += "\n\t" + _counter + " (" + _errors +" errors)";
	}

	private void tryToDoIt() {
        try {
            doIt();
        } catch (RuntimeException e) {
            _errors++;
            //e.printStackTrace();
        }
        
    }

    private void doIt() {

		System.err.println("------------------------------------------");
		initState();
		System.err.println("--------------");
		
        performChanges();
        
		ReplicationProcess replication = _containerA.ext().replicationBegin(_containerB, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });

		if (_direction.size() == 1) {
			if (_direction.contains(_containerA)) {System.err.println("Setting direction TO A.");replication.setDirection(_containerB, _containerA);}
			if (_direction.contains(_containerB)) {System.err.println("Setting direction TO B.");replication.setDirection(_containerA, _containerB);}
		}

		
        if (_containersToQueryFrom.contains(_containerA)) {
			replicateQueryingFrom(replication, _containerA);
        }
        if (_containersToQueryFrom.contains(_containerB)) {
			replicateQueryingFrom(replication, _containerB);
        }
		
		replication.commit();

		checkNames();
	}

    private void checkNames() {
		checkNames(_containerA, _containerA);
		checkNames(_containerA, _containerB);
		checkNames(_containerB, _containerA);
		checkNames(_containerB, _containerB);
    }

	private void checkNames(ObjectContainer origin, ObjectContainer inspected) {
		checkName(inspected, "oldFrom" + letter(origin), isOldNameExpected(origin, inspected));
		checkName(inspected, "newFrom" + letter(origin), isNewNameExpected(origin, inspected));
		checkName(inspected, "oldFromAChangedIn" + letter(origin), isChangedNameExpected(origin, inspected));
		checkName(inspected, "oldFromBChangedIn" + letter(origin), isChangedNameExpected(origin, inspected));
	}

	private boolean isOldNameExpected(ObjectContainer origin, ObjectContainer inspected) {
		if (_containersWithChangedObjects.contains(inspected)) return false;
		if (!_containersWithChangedObjects.contains(other(inspected))) return true;
		if (!_direction.contains(inspected)) return true;
		if (!_containersToQueryFrom.contains(other(inspected))) return true;
		return false;
	}

	private ObjectContainer other(ObjectContainer container) {
		return container == _containerA ? _containerB  : _containerA;
	}

	private boolean isNewNameExpected(ObjectContainer origin, ObjectContainer inspected) {
		if (_containersWithNewObjects.contains(inspected) && origin == inspected) return true;
		return false;
	}

	private boolean isChangedNameExpected(ObjectContainer origin, ObjectContainer inspected) {
		if (_containersWithChangedObjects.contains(inspected) && origin == inspected) return true;
		return false;
	}

	private String letter(ObjectContainer origin) {
		return origin == _containerA ? "A"  : "B";
	}

	private void performChanges() {

		if (_containersWithNewObjects.contains(_containerA)) {
			_containerA.set(new Replicated("newFromA"));
        }
		if (_containersWithNewObjects.contains(_containerB)) {
			_containerB.set(new Replicated("newFromB"));
        }
		
		
		if (_containersWithChangedObjects.contains(_containerA)) {
            changeObject(_containerA, "oldFromA", "oldFromAChangedInA");
            changeObject(_containerA, "oldFromB", "oldFromBChangedInA");
        }
		if (_containersWithChangedObjects.contains(_containerB)) {
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

    private void addNames(Set names, String[] newNames) {
		for (int i = 0; i < newNames.length; i++) {
			names.add(newNames[i]);
		}
	}

	private void checkName(ObjectContainer container, String name, boolean isExpected) {
        System.err.println("" + _counter + " " + name + (isExpected ? " expected" : " not expected") + (container == _containerA ? " in A" : " in B" ));
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

		ensure(objectsToReplicate(replication, _containerA).size() == 0);
		ensure(objectsToReplicate(replication, _containerB).size() == 0);
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
			System.err.println("Replicating: " + next);
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
		Iterator iter = containerSet.iterator();
		String result = "" + letter((ObjectContainer)iter.next());
		if (!iter.hasNext()) return result;
		return result + ", " + letter((ObjectContainer)iter.next());
	}

	
}
