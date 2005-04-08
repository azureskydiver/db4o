/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;


public class GenericFeatures {
    
    String hi;
    
    String[] names;
    
    
    
    public void storeOne(){
        hi = "Hi Carl, Dave, Klaus, Patrick and Rodrigo";
        names = new String[]{
            "Rodrigo",
            "Patrick",
            "Klaus",
            "Dave",
            "Carl"
        };
    }
    
   

}
