/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.*;
import com.db4o.inside.replication.*;
import com.db4o.replication.*;
import com.db4o.test.*;


public abstract class ReplicationTestcase {
    
    private long _timer;
    
    protected abstract TestableReplicationProvider prepareProviderA();
    protected abstract TestableReplicationProvider prepareProviderB();
    
    protected TestableReplicationProvider _providerA;
    protected TestableReplicationProvider _providerB;
    
    protected void init(){
        _providerA = prepareProviderA();
        _providerB = prepareProviderB();
    }
    
    protected void delete(Class[] classes){
        for (int i = 0; i < classes.length; i++) {
            _providerA.delete(classes[i]);
            _providerB.delete(classes[i]);
        }
        _providerA.commit();
        _providerB.commit();
    }
    
    protected void replicateAll(TestableReplicationProvider providerFrom, TestableReplicationProvider providerTo){
        ReplicationSession replication = Replication.begin(providerFrom, providerTo);
        ObjectSet allObjects = providerFrom.objectsChangedSinceLastReplication();
        while(allObjects.hasNext()){
            replication.replicate(allObjects.next());
        }
        replication.commit();
    }
    
    protected void ensureOneInstance(TestableReplicationProvider provider, Class clazz){
        ensureInstanceCount(provider, clazz, 1);
    }
    
    protected void ensureInstanceCount(TestableReplicationProvider provider, Class clazz, int count){
        ObjectSet objectSet = provider.getStoredObjects(clazz);
        while(objectSet.hasNext()){
            count --;
            objectSet.next();
        }
        if(count != 0){
            int xxx = 1;
        }
        Test.ensure(count == 0);
    }
    
    protected void replicateClass(TestableReplicationProvider providerA, TestableReplicationProvider providerB, Class clazz){
        ReplicationSession replication = Replication.begin(providerA, providerB);
        ObjectSet allObjects = providerA.objectsChangedSinceLastReplication(clazz);
        while(allObjects.hasNext()){
            replication.replicate(allObjects.next());
        }
        replication.commit();
    }
    
    protected Object getOneInstance(TestableReplicationProvider provider, Class clazz){
        ObjectSet objectSet = provider.getStoredObjects(clazz);
        return objectSet.next();
    }
    
    protected void startTimer(){
        _timer = System.currentTimeMillis();
    }
    
    protected void logTime(String msg){
        long time = System.currentTimeMillis();
        long duration = time - _timer;
        System.out.println(msg + " " + duration + "ms");
        _timer = System.currentTimeMillis();
    }
    
    

}
