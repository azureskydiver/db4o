/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.defragment;


public class SCDFirstClass {
    
    public SCDSecondClass _child;
    
    public SCDFirstClass(){
        
    }
    
    public SCDFirstClass(SCDSecondClass child){
        _child = child;
    }

}
