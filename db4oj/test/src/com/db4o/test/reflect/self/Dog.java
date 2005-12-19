/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.reflect.self;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.reflect.self.*;
import com.db4o.test.*;


public class Dog extends Animal {

	private transient static List dogs;
	
    // must be public for the time being due to setAccessible() check in Platform4
    public int _age;
    
    public Dog[] _parents;
    
    public Dog() {
    	// require public no-args constructor
    	this(null,0);
    }
    
    public Dog(String name,int age){
    	this(name,age,new Dog[0]);
    }

    public Dog(String name,int age,Dog[] parents){
    	super(name);
        _age = age;
        _parents=parents;
    }
    
    public void configure(){
        Db4o.configure().reflectWith(new SelfReflector(new RegressionDogSelfReflectionRegistry()));
    }
    
    public void store(){
    	dogs=new ArrayList();
    	Dog laika = new Dog("Laika",7);
    	Dog lassie = new Dog("Lassie",6);
		dogs.add(laika);
		dogs.add(lassie);
    	dogs.add(new Dog("Sharik",100, new Dog[]{laika,lassie}));
    	for (Iterator iter = dogs.iterator(); iter.hasNext();) {
    		Test.store(iter.next());
		}
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(Dog.class);
        ObjectSet res = q.execute();
        Test.ensure(res.size() == dogs.size());
        while(res.hasNext()) {
        	Dog dog=(Dog)res.next();
        	System.out.println(">>>"+dog._name);
        	Test.ensure(dogs.contains(dog));
        }
                
        q = Test.query();
        q.constrain(Dog.class);
        q.descend("_name").constrain("Laika");
        res = q.execute();
        Test.ensure(res.size() == 1);
        Dog laika = (Dog) res.next();
        Test.ensure(laika._name.equals("Laika"));
    }

	public Object self_get(String fieldName) {
		if(fieldName.equals("_age")) {
			return new Integer(_age);
		}
		if(fieldName.equals("_parents")) {
			return _parents;
		}
		return super.self_get(fieldName);
	}

	public void self_set(String fieldName,Object value) {
		if(fieldName.equals("_age")) {
			_age=((Integer)value).intValue();
			return;
		}
		if(fieldName.equals("_parents")) {
			_parents=(Dog[])value;
			return;
		}
		super.self_set(fieldName,value);
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		Dog dog=(Dog)obj;
		boolean sameName=(_name==null ? dog._name==null : _name.equals(dog._name));
		boolean sameAge=_age==dog._age;
		boolean sameParentLength=_parents.length==dog._parents.length;
		return sameName&&sameAge&&sameParentLength;
	}
	
	public int hashCode() {
		int hash=_age;
		hash=hash*29+(_name==null ? 0 : _name.hashCode());
		hash=hash*29+_parents.length;
		return hash;
	}
}
