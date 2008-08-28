/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.aliases;


/**
 * @decaf.ignore.jdk11
 */
public class Parent2 {
    
    public Child2 child;
    
    public Parent2(){
    }
    
    public Parent2(Child2 child){
        this.child = child;
    }


}
