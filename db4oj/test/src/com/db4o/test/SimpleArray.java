/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

/**
 * 
 */
class SimpleArray {
    
    String[] arr;
    
    public void storeOne(){
        arr = new String[] {"hi", "babe"};
    }
    
    public void testOne(){
        Test.ensure(arr.length == 2);
        Test.ensure(arr[0].equals("hi"));
        Test.ensure(arr[1].equals("babe"));
    }

}
