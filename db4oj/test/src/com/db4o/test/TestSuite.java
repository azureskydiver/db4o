/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;


/**
 * 
 */
public abstract class TestSuite {
    
    public abstract Class[] tests();
    
    public static TestSuite suite(String name){
        try{
            Class clazz = Class.forName(name);
            if(clazz != null){
                TestSuite ts = (TestSuite)clazz.newInstance();
                return ts;
            }
        }catch(Exception e){
            
        }
        return null;
    }

}
