/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

public class GenericFeatures {
    
    String hi;
    String[] names;
	
	SomeUnavailableClass willTooBeAvailable;
	
	SomeUnavailableClass[] willTooBeAvailables;
    
    Object someObject;
    Object[] someObjectArray;
    
    Object someMessInAnObject;
    
    
    public void store(){
        for (int i = 0; i < 10000; i++) {
            GenericFeatures gf = new GenericFeatures();
            gf.setVals(i);
            Test.objectContainer().set(gf);
        }
    }

    
    public void setVals(int ix){
        hi = "Hi Carl, Dave, Klaus, Patrick and Rodrigo";
        names = new String[]{
            "Rodrigo",
            "Patrick",
            "Klaus",
            "Dave",
            "Carl"
        };
		
		willTooBeAvailable = new SomeUnavailableClass("willToo");
		
		willTooBeAvailables = new SomeUnavailableClass[] {
				new SomeUnavailableClass("willTooOne"),
				new SomeUnavailableClass("willTooTwo")
		};
        
        someObject = new SomeUnavailableClass("forSomeObject");
        someObjectArray = new Object[] {
            new SomeUnavailableClass("in someObjectArray"),
            "Jadow"
        };
        
        someMessInAnObject = new Object[] {
            new SomeUnavailableClass("in some mess"),
            "Jadowrow",
            new Integer(42)
        };
        
        
    }
    

}
