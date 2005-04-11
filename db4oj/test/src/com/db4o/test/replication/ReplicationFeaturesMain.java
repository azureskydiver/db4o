/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;

public class ReplicationFeaturesMain {

    private static final String FILE_A = "replicationTestA.yap";
    private static final String FILE_B = "replicationTestB.yap";
    private static ExtObjectContainer _a;
    private static ExtObjectContainer _b;

    public static void main(String[] ignored) {
        init();

        test();
        
        exit();
    }
    
    private static void exit() {
        _a.close();
        _b.close();
        
        System.out.println("Done.");
        System.exit(0);
    }

    public static void init(){
        new File(FILE_A).delete();
        new File(FILE_B).delete();
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
        _a = Db4o.openFile(FILE_A).ext();
        _b = Db4o.openFile(FILE_B).ext();
    }
    
    public static void test() {
        _a.set(new Replicated("a1"));
        _a.commit();

        _b.set(new Replicated("b1"));
        _b.commit();

        replicateInBothDirections();

        System.out.println("Checking a -> b");
        checkAllEqual(_a, _b);
        System.out.println("Checking b -> a");
        checkAllEqual(_b, _a);

//      TODO: replication.setDirection(...);
//      TODO: cascading
//      TODO: replication.checkConflict(obj); //(peek)

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
        
        ObjectSet all = con1.get(null);
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
