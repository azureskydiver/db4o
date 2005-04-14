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
    
    private static ReplicationConflictHandler _ignoreConflictHandler;
    
    private static final int LINKERS = 4;
    
    
    public void configure(){
        uUIDsOn(R0.class);
        uUIDsOn(R1.class);
        uUIDsOn(R2.class);
        uUIDsOn(R3.class);
        uUIDsOn(R4.class);
        
        _ignoreConflictHandler = new ReplicationConflictHandler() {
            public Object resolveConflict(
                ReplicationProcess replicationProcess, 
                Object a, 
                Object b) {
                return null;
            }
        }; 
    }
    
    private void uUIDsOn(Class clazz){
        Db4o.configure().objectClass(clazz).generateUUIDs(true);
        Db4o.configure().objectClass(clazz).generateVersionNumbers(true);
    }
    
    public void store(){
        _peerA = Test.objectContainer();
        
        R0Linker lCircles = new R0Linker();
        lCircles.setNames("circles");
        lCircles.linkCircles();
        lCircles.store(_peerA);
        
        R0Linker lList = new R0Linker();
        lList.setNames("list");
        lList.linkList();
        lList.store(_peerA);
        
        R0Linker lThis = new R0Linker();
        lThis.setNames("this");
        lThis.linkThis();
        lThis.store(_peerA);
        
        R0Linker lBack = new R0Linker();
        lBack.setNames("back");
        lBack.linkBack();
        lBack.store(_peerA);
        
    }
    
    public void test(){
        _peerA = Test.objectContainer();
        _peerB = Test.replica();
        
        ensureCount(_peerA, LINKERS);
        
        copyAllToB();
        ensureNoneModified();
        
        modifyR4(_peerA);
        
        
        
    }
    
    private void modifyR4(ObjectContainer oc){
        Query q = oc.query();
        q.constrain(R4.class);
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            R4 r4 = (R4)objectSet.next();
            r4.name = r4.name + "_";
            oc.set(r4);
        }
    }
    
    
    private void copyAllToB(){
        Test.ensure(replicateAll(false) == LINKERS * 5);
    }
    
    private void ensureNoneModified(){
        Test.ensure(replicateAll() == 0);
    }
    
    
    private int replicateAll(){
        return replicateAll(true);
    }
    
    private int replicateAll(boolean modifiedOnly){
        ReplicationProcess replication = _peerA.replicationBegin(_peerB, _ignoreConflictHandler); 
        Query q = _peerA.query();
        q.constrain(R0.class);
        if(modifiedOnly){
            replication.whereModified(q);
        }
        ObjectSet objectSet = q.execute();
        int replicated = 0;
        while(objectSet.hasNext()){
            replication.replicate(objectSet.next());
            replicated ++;
        }
        replication.commit();
        ensureCount(_peerA,LINKERS);
        ensureCount(_peerB,LINKERS);
        return replicated;
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
