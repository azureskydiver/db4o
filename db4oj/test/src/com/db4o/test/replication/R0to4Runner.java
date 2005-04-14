/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;


public class R0to4Runner {
    
    private ExtObjectContainer _peerA;
    private ExtObjectContainer _peerB;
    
    
    public void configure(){
        uUIDsOn(R0.class);
        uUIDsOn(R1.class);
        uUIDsOn(R2.class);
        uUIDsOn(R3.class);
        uUIDsOn(R4.class);
    }
    
    private void uUIDsOn(Class clazz){
        Db4o.configure().objectClass(clazz).generateUUIDs(true);
        Db4o.configure().objectClass(clazz).generateVersionNumbers(true);
    }
    
    
    
    public void store(){
        ExtObjectContainer oc = Test.objectContainer();
        R0Linker lCircles = new R0Linker();
        lCircles.setNames("circles");
        lCircles.linkCircles();
        lCircles.store(oc);
    }
    
    public void test(){
        _peerA = Test.objectContainer();
        _peerB = Test.replica();

        ensureCount(_peerA, 1);
        
        ReplicationProcess replication = 
            _peerA.replicationBegin(_peerB, new ReplicationConflictHandler() {
        
            public Object resolveConflict(
                ReplicationProcess replicationProcess, 
                Object a, 
                Object b) {
                
                return null;
            }
        
        });
        
        Query q = _peerA.query();
        q.constrain(R0.class);
        // replication.whereModified(q);
        
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            replication.replicate(objectSet.next());
        }
        
        replication.commit();
        ensureCount(_peerA,1);
        ensureCount(_peerB,1);
        
        
        replication = 
            _peerA.replicationBegin(_peerB, new ReplicationConflictHandler() {
        
            public Object resolveConflict(
                ReplicationProcess replicationProcess, 
                Object a, 
                Object b) {
                
                return null;
            }
        
        });
        
        q = _peerA.query();
        q.constrain(R0.class);
        // replication.whereModified(q);
        
        objectSet = q.execute();
        while(objectSet.hasNext()){
            replication.replicate(objectSet.next());
        }
        
        replication.commit();
        ensureCount(_peerA,1);
        ensureCount(_peerB,1);
        
        
        
        
        
        
        
    }
    
    private void ensureCount(ObjectContainer oc, int linkers){
        ensureCount(oc, R0.class, linkers * 5);
        ensureCount(oc, R1.class, linkers * 4);
        ensureCount(oc, R2.class, linkers * 3);
        ensureCount(oc, R3.class, linkers * 2);
        ensureCount(oc, R4.class, linkers * 1);
    }
    
    private void ensureCount(ObjectContainer oc, Class clazz, int count){
        Query q = oc.query();
        q.constrain(clazz);
        Test.ensure(q.execute().size() == count);
    }
    
    

}
