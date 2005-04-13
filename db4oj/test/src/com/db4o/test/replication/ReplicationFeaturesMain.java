/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;

public class ReplicationFeaturesMain {

    private static final String FILE_B = "replicationTestB.yap";
    private static ExtObjectContainer _a;
    private static ExtObjectContainer _b;

    
    private static void exit() {
        _a.close();
        _b.close();
        
        System.out.println("Done.");
        System.exit(0);
    }

    public void configure(){
        new File(FILE_B).delete();
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
        _b = Db4o.openFile(FILE_B).ext();
    }
	
	private void setUpObjectContainers() {
		_a = Test.objectContainer();
		_b = Db4o.openFile(FILE_B).ext();
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
		
		
		

        replicateInBothDirections();

        System.out.println("Checking a -> b");
        checkAllEqual(_a, _b);
        System.out.println("Checking b -> a");
        checkAllEqual(_b, _a);

//      TODO: replication.setDirection(...);
//      TODO: cascading
//      TODO: replication.checkConflict(obj); //(peek)

		
		_b.close();
    }

    private static void replicateInBothDirections(){
        final ReplicationProcess replication = _a.replicationBegin(_b, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });

        replicate(replication, _a);
        replicate(replication, _b);
        replication.commit();
    }

    private static void replicate(ReplicationProcess replication, ExtObjectContainer origin) {
        Query q = origin.query();
        q.constrain(Replicated.class);
        replication.whereModified(q);
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            Object obj = objectSet.next();
            Db4oUUID uuid = origin.getObjectInfo(obj).getUUID();

            System.out.println("Replicating:" + obj);
            replication.replicate(obj);
        }
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

            System.out.println("Comparing: " + obj1 + "  to: " + obj2);
            
            Test.ensure(comparator.isEqual(obj1, obj2));
        }
    }
    
}
