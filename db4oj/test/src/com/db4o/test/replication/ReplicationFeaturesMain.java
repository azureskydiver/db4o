/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;

public class ReplicationFeaturesMain {

    private static ExtObjectContainer _a;
    private static ExtObjectContainer _b;

    
    public void configure(){
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
    }
	
	private void setUpObjectContainers() {
		_a = Test.objectContainer();
		_b = Test.replica();
	}
	
	public void store() {
		setUpObjectContainers();
		
        _a.set(new Replicated("a1"));
        _a.commit();

        _b.set(new Replicated("b1"));
        _b.commit();
		
		
		_b.close();
	}
    
    public void test() {
		setUpObjectContainers();

		final ReplicationProcess replication = _a.replicationBegin(_b, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });

        replicateInBothDirections(replication);

        checkAllEqual(_a, _b);
        checkAllEqual(_b, _a);
		
		Test.ensure(objectsToReplicate(replication, _a).size() == 0);
		Test.ensure(objectsToReplicate(replication, _b).size() == 0);
	
//      TODO: replication.setDirection(...);
//      TODO: replication.checkConflict(obj); //(peek)
		
    }

    private static void replicateInBothDirections(ReplicationProcess replication){
        replicate(replication, _a);
        replicate(replication, _b);
        replication.commit();
    }

    private static void replicate(ReplicationProcess replication, ExtObjectContainer origin) {
        ObjectSet objectSet = objectsToReplicate(replication, origin);
        while(objectSet.hasNext()){
            Object obj = objectSet.next();
            Db4oUUID uuid = origin.getObjectInfo(obj).getUUID();

            replication.replicate(obj);
        }
    }

	private static ObjectSet objectsToReplicate(ReplicationProcess replication, ExtObjectContainer origin) {
		Query q = origin.query();
		q.constrain(Replicated.class);
		replication.whereModified(q);
		ObjectSet objectSet = q.execute();
		return objectSet;
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
