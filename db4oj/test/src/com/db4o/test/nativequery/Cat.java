/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class Cat {
    
    public String name;
    
    public Cat(){
        
    }
    
    public Cat(String name){
        this.name = name;
    }
    
    public void store(){
        Test.deleteAllInstances(Cat.class);
        Test.store(new Cat("Fritz"));
        Test.store(new Cat("Garfield"));
        Test.store(new Cat("Tom"));
        Test.store(new Cat("Occam"));
        Test.store(new Cat("Zora"));
    }
    
    public void test(){
        ObjectContainer objectContainer = Test.objectContainer();
        ObjectSet objectSet = objectContainer.query(new CatPredicate());
        Test.ensure(objectSet.size() == 2);
        String[] lookingFor = new String[] {"Occam" , "Zora"};
        boolean[] found = new boolean[2];
        while(objectSet.hasNext()){
            Cat cat = (Cat)objectSet.next();
            for (int i = 0; i < lookingFor.length; i++) {
                if(cat.name.equals(lookingFor[i])){
                    found[i] = true;
                }
            }
        }
        for (int i = 0; i < found.length; i++) {
            Test.ensure(found[i]);
        }
    }
    
    public static class CatPredicate extends Predicate{
        public boolean match(Cat cat){
            return cat.name.equals("Occam") || cat.name.equals("Zora"); 
        }
    }

}
