/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;

public class ReplicationFeaturesMain {

	private static final Set A = new HashSet(1); 
	private static final Set B = new HashSet(1); 
	private static final Set BOTH = new HashSet(2); 
	private static final Set NONE = Collections.EMPTY_SET; 
	
	{
		A.add("A");
		B.add("B");
		BOTH.add("A");
		BOTH.add("B");
	}
	
    private static ObjectContainer _a;
    private static ObjectContainer _b;
	
	private Set _direction;
	private Set _containersToQueryFrom;
	private Set _containersWithNewObjects;
	private Set _containersWithChangedObjects;
	private Set _objectsToPrevailInConflicts;

	private Set _impossibleNamesInA;
	private Set _impossibleNamesInB;
	
	private static final String[] ALL_POSSIBLE_NAMES = new String[]{"oldInA", "oldInB", "newInA", "newInB", "oldInAChangedInA", "oldInAChangedInB", "oldInBChangedInA", "oldInBChangedInB"};
	private static final String[] NAME_CHANGES_IN_A = new String[]{"newInA", "oldInAChangedInA", "oldInBChangedInA"};
	private static final String[] NAME_CHANGES_IN_B = new String[]{"newInB", "oldInAChangedInB", "oldInBChangedInB"};
	
    
    public void configure(){
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
    }
	
    public void test() {
        
		_a = Test.objectContainer();
		_b = Test.replica();


		deleteAll(_a);
		deleteAll(_b);

		_a.set(new Replicated("Whatever"));
		ReplicationProcess replication = _a.ext().replicationBegin(_b, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });

		replication.setDirection(_b, _a);
		
		Test.ensure(!_b.get(null).hasNext());
		replicateQueryingFrom(replication, _a);
		replication.commit();
		Test.ensure(!_b.get(null).hasNext());


		
/*	WORK IN PROGRESS:	
		tstDirection(A);
		tstDirection(B);
		tstDirection(BOTH);
*/
		
//      TODO: replication.checkConflict(obj); //(peek)
		
    }

	private void tstDirection(Set direction) {
		_direction = direction;
		
		tstQueryingFrom(A);
		tstQueryingFrom(B);
		tstQueryingFrom(BOTH);
	}

	private void tstQueryingFrom(Set containers) {
		_containersToQueryFrom = containers;
		
		tstWithNewObjectsIn(NONE);
		tstWithNewObjectsIn(A);
		tstWithNewObjectsIn(B);
		tstWithNewObjectsIn(BOTH);
	}

	private void tstWithNewObjectsIn(Set containers) {
		_containersWithNewObjects = containers;
		
		tstWithChangedObjectsIn(NONE);
		tstWithChangedObjectsIn(A);
		tstWithChangedObjectsIn(B);
		tstWithChangedObjectsIn(BOTH);
	}

	private void tstWithChangedObjectsIn(Set containers) {
		_containersWithChangedObjects = containers;
		
		tstWithObjectsPrevailingConflicts(NONE);
		tstWithObjectsPrevailingConflicts(A);
		tstWithObjectsPrevailingConflicts(B);
	}


	private void tstWithObjectsPrevailingConflicts(Set containers) {
		_objectsToPrevailInConflicts = containers;
		
		System.err.println(">>>>>>>>>>>>>>>> 1");
		doIt();
		System.err.println(">>>>>>>>>>>>>>>> 2");
		doIt();
		System.err.println(">>>>>>>>>>>>>>>> 3");
		doIt();
	}

	private void doIt() {

		System.err.println("------------------------------------------");

		initState();
		System.err.println("--------------");
		
        setNameExpectations();
        
		ReplicationProcess replication = _a.ext().replicationBegin(_b, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });

		if (_direction.size() == 1) {
			if (_direction.contains("A")) {System.err.println("Setting direction TO A.");replication.setDirection(_b, _a);}
			if (_direction.contains("B")) {System.err.println("Setting direction TO B.");replication.setDirection(_a, _b);}
		}

		
        if (_containersToQueryFrom.contains("A")) {
			replicateQueryingFrom(replication, _a);
        }
        if (_containersToQueryFrom.contains("B")) {
			replicateQueryingFrom(replication, _b);
        }
		
		replication.commit();

		workaroundBugs(); 
		
		checkNames(_a, _impossibleNamesInA);
		checkNames(_b, _impossibleNamesInB);
	}

	private void workaroundBugs() {
		System.err.println("Working around bugs.");
		if (!_direction.contains("A")) {
			Object obj = find(_a, "newInB");
			_a.delete(obj);
			_a.commit();
		}
		if (!_direction.contains("B")) {
			Object obj = find(_b, "newInA");
			_b.delete(obj);
			_b.commit();
		}
	}

	private void setNameExpectations() {
		_impossibleNamesInA = new HashSet();
		_impossibleNamesInB = new HashSet();
		
		
		if (!_direction.contains("A")) {
            addNames(_impossibleNamesInA, NAME_CHANGES_IN_B);
        }
		if (!_direction.contains("B")) {
            addNames(_impossibleNamesInB, NAME_CHANGES_IN_A);
        }
		
		
		if (!_containersToQueryFrom.contains("A")) {
			_impossibleNamesInB.add("newInA");
			_impossibleNamesInB.add("oldInAChangedInA");
			_impossibleNamesInB.add("oldInBChangedInA");
		}
		if (!_containersToQueryFrom.contains("B")) {
			_impossibleNamesInA.add("newInB");
			_impossibleNamesInA.add("oldInAChangedInB");
			_impossibleNamesInA.add("oldInBChangedInB");
		}
		
		
		if (_containersWithNewObjects.contains("A")) {
			_a.set(new Replicated("newInA"));
		} else {
            _impossibleNamesInA.add("newInA");
            _impossibleNamesInB.add("newInA");
        }
		if (_containersWithNewObjects.contains("B")) {
			_b.set(new Replicated("newInB"));
		} else {
            _impossibleNamesInA.add("newInB");
            _impossibleNamesInB.add("newInB");
        }
		
		
		if (_containersWithChangedObjects.contains("A")) {
            changeObject(_a, "oldInA", "oldInAChangedInA");
            changeObject(_a, "oldInB", "oldInBChangedInA");
            _impossibleNamesInA.add("oldInA");
            _impossibleNamesInA.add("oldInB");
        } else {
            _impossibleNamesInA.add("oldInAChangedInA");
            _impossibleNamesInA.add("oldInBChangedInA");
            _impossibleNamesInB.add("oldInAChangedInA");
            _impossibleNamesInB.add("oldInBChangedInA");
        }
		if (_containersWithChangedObjects.contains("B")) {
            changeObject(_b, "oldInA", "oldInAChangedInB");
            changeObject(_b, "oldInB", "oldInBChangedInB");
            _impossibleNamesInB.add("oldInA");
            _impossibleNamesInB.add("oldInB");
        } else {
            _impossibleNamesInA.add("oldInAChangedInB");
            _impossibleNamesInA.add("oldInBChangedInB");
            _impossibleNamesInB.add("oldInAChangedInB");
            _impossibleNamesInB.add("oldInBChangedInB");
        }
		
		_a.commit();
		_b.commit();
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


	private void checkNames(ObjectContainer container, Set impossibleNames) {
	    for (int i = 0; i < ALL_POSSIBLE_NAMES.length; i++) {
            String name = ALL_POSSIBLE_NAMES[i];
            check(container, name, !impossibleNames.contains(name));
        }
	}

	private static int counter;
	private void check(ObjectContainer container, String name, boolean isExpected) {
        System.err.println("" + ++counter + " " + name + " " + isExpected + (container == _a ? " in A" : " in B" ));
        Replicated obj = find(container, name);
        if (isExpected) {
            Test.ensure(obj != null);
//            container.delete(obj);
        } else {
            Test.ensure(obj == null);
        }
	}

    private Replicated find(ObjectContainer container, String name) {
        Query q = container.query();
        q.constrain(Replicated.class);
        q.descend("_name").constrain(name);
        ObjectSet set = q.execute();
        Test.ensure(set.size() < 2);
        return (Replicated)set.next();
    }
    
	private void initState() {
		_a.commit();
		_b.commit();
		deleteAll(_a);
		deleteAll(_b);

		_a.set(new Replicated("oldInA"));
		_b.set(new Replicated("oldInB"));
		
		_a.commit();
		_b.commit();

		final ReplicationProcess replication = _a.ext().replicationBegin(_b, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });

        replicateQueryingFrom(replication, _a);
		replicateQueryingFrom(replication, _b);
		replication.commit();

		Test.ensure(objectsToReplicate(replication, _a).size() == 0);
		Test.ensure(objectsToReplicate(replication, _b).size() == 0);
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

            Test.ensure(comparator.isEqual(obj1, obj2));
        }
    }
    
}
