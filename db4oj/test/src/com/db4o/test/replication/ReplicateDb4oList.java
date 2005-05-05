/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;


public class ReplicateDb4oList {
    
    List list;
    
    public void configure(){
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
    }
    
    public void storeOne(){
        list = Test.objectContainer().collections().newLinkedList();
        list.add(new RDLElement("store1"));
        list.add(new RDLElement("store2"));
    }
    
    public void testOne(){
        
        replicate(false);
        
        ObjectContainer oc = Test.replica();
        Query q = oc.query();
        q.constrain(ReplicateDb4oList.class);
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == 1);
        ReplicateDb4oList rdl = (ReplicateDb4oList)objectSet.next();
        RDLElement elem = (RDLElement)rdl.list.get(0);
        Test.ensure(elem.name.equals("store1"));
        elem = (RDLElement)rdl.list.get(1);
        Test.ensure(elem.name.equals("store2"));
        
        // Test.reOpen();
        
        list.add(new RDLElement("afterReplication"));
        elem = (RDLElement)list.get(0);
        elem.name = "replicated";
        Test.store(elem);
        
        // storing this to make sure it is dirty
        Test.store(this);
        
        replicate(true);
        
        oc = Test.replica();
        q = oc.query();
        q.constrain(ReplicateDb4oList.class);
        objectSet = q.execute();
        Test.ensure(objectSet.size() == 1);
        rdl = (ReplicateDb4oList)objectSet.next();
        elem = (RDLElement)rdl.list.get(0);
        Test.ensure(elem.name.equals("replicated"));
        elem = (RDLElement)rdl.list.get(1);
        Test.ensure(elem.name.equals("store2"));
        elem = (RDLElement)rdl.list.get(2);
        Test.ensure(elem.name.equals("afterReplication"));
    }
    
    private void replicate(boolean modifiedOnly) {
        ExtObjectContainer peerA = Test.objectContainer().ext();
        ObjectContainer peerB = Test.replica();
        ReplicationProcess replication = peerA.replicationBegin(peerB, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess replicationProcess, Object a, Object b) {
                return null;
            }
        });
        Query q = peerA.query();
        q.constrain(ReplicateDb4oList.class);
        if (modifiedOnly) {
            replication.whereModified(q);
        }
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
            ReplicateDb4oList rdl = (ReplicateDb4oList)objectSet.next(); 
            replication.replicate(rdl);
            // replication.replicate(rdl.list);
        }
        replication.commit();
    }

    
    public static class RDLElement{
        
        private String name;
        
        public RDLElement(String name){
            this.name = name;
        }
    }
}
