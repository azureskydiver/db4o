/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.aliases;


/**
 * @decaf.ignore.jdk11
 */
public class Parent1 {
    
    public Child1 child;
    
    public Parent1(){
    }
    
    public Parent1(Child1 child){
        this.child = child;
    }


}
