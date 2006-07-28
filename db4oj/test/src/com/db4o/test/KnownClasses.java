/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.reflect.*;

public class KnownClasses {
    
    public static final Class[] NOT_WANTED = new Class[] {
        Db4oDatabase.class,
        PBootRecord.class,
        StaticClass.class,
        MetaClass.class,
        MetaField.class
    };
    
    public void storeOne(){
        // make sure there is one in the database
    }
	
	public void test(){
        boolean found = false;
		ReflectClass[] knownClasses = Test.objectContainer().knownClasses();
		for (int i = 0; i < knownClasses.length; i++) {
            if(knownClasses[i].isPrimitive()){
                Test.error();
            }
            if(knownClasses[i].isSecondClass()){
                Test.error();
            }
            if(knownClasses[i].getName().equals(this.getClass().getName())){
                found = true;
            }
            for (int j = 0; j < NOT_WANTED.length; j++) {
                if(knownClasses[i].getName().equals(NOT_WANTED[j].getName())){
                    Test.error();
                }
            }
		}
        Test.ensure(found);
	}

}
