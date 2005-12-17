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
    
    public Dog() {
    	// require public no-args constructor
    	this(null,0);
    }
    
    public Dog(String name,int age){
    	super(name);
        _age = age;
    }
    
    public void configure(){
        Db4o.configure().reflectWith(new SelfReflector(new RegressionDogSelfReflectionRegistry()));
    }
    
    public void store(){
    	dogs=new ArrayList();
    	dogs.add(new Dog("Laika",7));
    	dogs.add(new Dog("Lassie",6));
    	dogs.add(new Dog("Sharik",100));
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
		return super.self_get(fieldName);
	}

	public void self_set(String fieldName,Object value) {
		if(fieldName.equals("_age")) {
			_age=((Integer)value).intValue();
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
		if(_name==null) {
			return dog._name==null;
		}
		return _name.equals(dog._name);
	}
	
	public int hashCode() {
		return (_name==null ? 0 : _name.hashCode());
	}
}
