/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.reflect.self;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.reflect.self.*;
import com.db4o.test.*;


public class Dog implements SelfReflectable {

	private transient static List dogs;
	
    // must be public for the time being due to setAccessible() check in Platform4
    public String _name;
    
    public Dog() {
    	// require public no-args constructor
    }
    
    public Dog(String name){
        _name = name;
    }
    
    public void configure(){
        Db4o.configure().reflectWith(new SelfReflector(new RegressionDogSelfReflectionRegistry()));
    }
    
    public void store(){
    	dogs=new ArrayList();
    	dogs.add(new Dog("Laika"));
    	dogs.add(new Dog("Lassie"));
    	dogs.add(new Dog("Sharik"));
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
		if(fieldName.equals("_name")) {
			return _name;
		}
		return null;
	}

	public void self_set(String fieldName,Object value) {
		if(fieldName.equals("_name")) {
			_name=(String)value;
		}
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
