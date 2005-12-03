/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.reflect.self;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.reflect.self.*;
import com.db4o.test.*;


public class Dog implements SelfReflectable {
    
    public String _name;
    
    public Dog() {
    	// require public no-args constructor
    }
    
    public Dog(String name){
        _name = name;
    }
    
    public void configure(){
    	Db4o.configure().callConstructors(true);
    	Db4o.configure().exceptionsOnNotStorable(true);
       	Db4o.configure().activationDepth(Integer.MAX_VALUE);
        //Db4o.configure().reflectWith(new SelfReflector(new RegressionDogSelfReflectionRegistry()));
    }
    
    public void store(){
        Test.store(new Dog("Laika"));
        Test.store(new Dog("Lassie"));
        Test.store(new Dog("Scharik"));
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(Dog.class);
        ObjectSet res = q.execute();
        Test.ensure(res.size() == 3);
        Dog dog = (Dog) res.next();
        Test.ensure(dog._name != null);
        
        q = Test.query();
        q.constrain(Dog.class);
        q.descend("_name").constrain("Laika");
        res = q.execute();
        Test.ensure(res.size() == 1);
        Dog laika = (Dog) res.next();
        Test.ensure(laika._name.equals("Laika"));
    }

	public Object db4o$get(String fieldName) {
		if(fieldName.equals("_name")) {
			return _name;
		}
		return null;
	}
}
