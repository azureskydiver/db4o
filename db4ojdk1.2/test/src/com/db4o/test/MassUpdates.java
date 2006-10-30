/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;

public class MassUpdates {
    
    public void storeOne(){
        Test.deleteAllInstances(MassUpdateHelper.class);
        for (int i = 0; i < 5; i++) {
            Test.store(new MassUpdateHelper("muh" + i));
        }
    }
    
    public void testOne(){
        
        Query q = Test.query();
        
        // simple mass update
        q.constrain(MassUpdateHelper.class);
        q.constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                ObjectContainer objectContainer = candidate.objectContainer();
                MassUpdateHelper muh = (MassUpdateHelper)candidate.getObject();
                muh.name = "update1";
                objectContainer.set(muh);
                objectContainer.commit();
            }
        });
        
        // Lazy queries require iterating through the ObjectSet,
        // otherwise the evaluations won't be triggered.
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
        	objectSet.next();
        }
        
        check("update1");
        
        
        // triggering mass updates with a singleton
        // complete server-side execution
        q = Test.query();
        q.constrain(this.getClass());
        q.constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                ObjectContainer objectContainer = candidate.objectContainer();
                Query q2 = objectContainer.query();
                q2.constrain(MassUpdateHelper.class);
                ObjectSet objectSet = q2.execute();
                while(objectSet.hasNext()){
                    MassUpdateHelper muh = (MassUpdateHelper)objectSet.next();
                    muh.name = "update2";
                    objectContainer.set(muh);
                }
                objectContainer.commit();
            }
        });
        objectSet = q.execute();
        while(objectSet.hasNext()){
        	objectSet.next();
        }
        
        check("update2");
    }
    
    private void check(String name){
        ObjectContainer objectContainer = Test.objectContainer();
        Query q = Test.query();
        q.constrain(MassUpdateHelper.class);
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            MassUpdateHelper muh = (MassUpdateHelper)objectSet.next();
            
            // make sure that all objects are reloaded, so we get 
            // no local caching side-effects in C/S mode
            objectContainer.deactivate(muh, Integer.MAX_VALUE);
            objectContainer.activate(muh, Integer.MAX_VALUE);
            Test.ensure(muh.name.equals(name));
        }
    }
    
    
    
    public static class MassUpdateHelper{
        
        public String name;
        
        public MassUpdateHelper(){
        }
        
        public MassUpdateHelper(String name){
            this.name = name;
        }
        
    }
}
