/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;

/**
 * 
 */
public class ReplicationFeatures {
    
    public static final String FILESOLO = "reptsolo.yap";
    public static final String FILESERVER = "reptserver.yap";
    
    public String name;
    
    public void configure(){
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
        new File(FILESOLO).delete();
        new File(FILESERVER).delete();
    }
    
    public void storeOne() {
        name = "rf1";
        Test.store(this);
        replicate(this);
        checkOne("rf1");
    }
    
    public void testOne() {
        if(Test.isClientServer()) {
            ExtObjectContainer clientObjectContainer = Test.objectContainer();
            ExtObjectContainer serverObjectContainer = Test.server().ext().objectContainer().ext();
            Test.ensure(clientObjectContainer.identity().equals(serverObjectContainer.identity()));
        }
        name = "rf2";
        Test.store(this);
        Test.commit();
        replicate(this);
        checkOne("rf2");
        replicateAll();
        checkAllEqual();
        ensureDb4oDatabaseSingle();
    }
    
    private void replicate(Object obj){
        ExtObjectContainer peerA = Test.objectContainer();
        ExtObjectContainer peerB = Db4o.openFile(file()).ext();
        ReplicationProcess replication = peerA.ext().replicationBegin(peerB, new ReplicationConflictHandler() {
        	
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            		
            	// the object was changed in both ObjectContainers since
            	// the last replication
            	
            	return null;
            	
            }
            
        });
        
        replication.replicate(obj);
        replication.commit();
        peerB.close();
    }
    
    private void replicateAll(){
        ExtObjectContainer peerA = Test.objectContainer();
        ExtObjectContainer peerB = Db4o.openFile(file()).ext();
        final ReplicationProcess replication = peerA.ext().replicationBegin(peerB, new ReplicationConflictHandler() {
        	
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            		
            	// the object was change in both ObjectContainers since
            	// the last replication
            	
            	return null;
            	
            }
            
        });
        
//      replication.setDirection(master, slave); //Default is bidirectional.

        Query q = peerA.query();
        q.constrain(ReplicationFeatures.class);
        replication.whereModified(q);
        
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            Object masterObject = objectSet.next();
            ObjectInfo masterInfo = peerA.getObjectInfo(masterObject);
            if(masterInfo != null){
                Db4oUUID uuid = masterInfo.getUUID();
                if(uuid != null){
                    Object masterDuplicate = peerA.getByUUID(uuid);
                    Test.ensure(masterObject == masterDuplicate);

                    // check version numbers and decide upon direction,
                    // depending which one changed after last synchronisation
                    replication.replicate(masterObject);
                    replication.checkConflict(masterObject); // Another option (peek).
                   
                    
                }
            }
        }
        replication.commit();
        peerB.close();
    }
    
    private void checkAllEqual(){
        DeepCompare comparator = new DeepCompare();
        ExtObjectContainer master = Test.objectContainer();
        ExtObjectContainer slave = Db4o.openFile(file()).ext();
        ObjectSet objectSet = master.get(null);
        while(objectSet.hasNext()){
            Object masterObject = objectSet.next();
	            ObjectInfo masterInfo = master.getObjectInfo(masterObject);
	            if(masterInfo != null){
	                Db4oUUID uuid = masterInfo.getUUID();
	                if(uuid != null){
	                    Object masterDuplicate = master.getByUUID(uuid);
	                    Test.ensure(masterObject == masterDuplicate);
	                    Object slaveObject = slave.getByUUID(uuid);
	                    master.activate(masterObject, Integer.MAX_VALUE);
	                    slave.activate(slaveObject, Integer.MAX_VALUE);
	                    Test.ensure(comparator.isEqual(masterObject, slaveObject));
	                }
	            }
        }
        slave.close();
    }
    
    
    private void checkOne(String name){
        ObjectContainer slave = Db4o.openFile(file());
        Query q = slave.query();
        q.constrain(this.getClass());
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == 1);
        ReplicationFeatures rf = (ReplicationFeatures) objectSet.next();
        Test.ensure(rf.name.equals(name));
        slave.close();
    }
    
    private void ensureDb4oDatabaseSingle(){
        Hashtable ht = new Hashtable();
        Object val = new Object();
        YapStream yapStream = ((YapStream)Test.objectContainer());
        yapStream.showInternalClasses(true);
        Query q = Test.query();
        q.constrain(Db4oDatabase.class);
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            Db4oDatabase d4b = (Db4oDatabase)objectSet.next();
            Test.ensure(ht.get(d4b.i_signature) == null);
            ht.put(d4b.i_signature, val);
        }
        yapStream.showInternalClasses(false);
    }
    
    private String file(){
        return Test.isClientServer() ? FILESERVER : FILESOLO;
    }

}
