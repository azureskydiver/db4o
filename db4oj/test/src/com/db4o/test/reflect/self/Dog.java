/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.reflect.self;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.reflect.self.*;
import com.db4o.test.*;


public class Dog implements SelfReflectable {
    
    private String _name;
    
    public Dog(){
        // for now: require default constructor
    }
    
    public Dog(String name){
        _name = name;
    }
    
    public void configure(){
        Db4o.configure().reflectWith(new SelfReflector());
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

   private final static String[] FIELDNAMES={"_name"};
   private final static Class[] FIELDTYPES={String.class};
    
	public String[] db4o$getFieldNames() {
		return FIELDNAMES;
	}

	public Class db4o$getFieldType(String fieldName) {
		for(int idx=0;idx<FIELDNAMES.length;idx++) {
			if(FIELDNAMES[idx].equals(fieldName)) {
				return FIELDTYPES[idx];
			}
		}
		return null;
	}
}
