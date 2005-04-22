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
        list.add(new RDLElement("afterReplication"));
        RDLElement elem = (RDLElement)list.get(0);
        elem.name = "replicated";
        Test.store(elem);
        replicate(true);
        
        ObjectContainer oc = Test.replica();
        Query q = oc.query();
        q.constrain(ReplicateDb4oList.class);
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == 1);
        ReplicateDb4oList rdl = (ReplicateDb4oList)objectSet.next();
        elem = (RDLElement)rdl.list.get(0);
        Test.ensure(elem.name.equals("replicated"));
        elem = (RDLElement)rdl.list.get(1);
        Test.ensure(elem.name.equals("store2"));
        elem = (RDLElement)rdl.list.get(0);
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
            replication.replicate(objectSet.next());
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
