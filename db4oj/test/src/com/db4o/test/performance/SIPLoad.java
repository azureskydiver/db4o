/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.performance;

public class SIPLoad {
    
    public String _name;
    
    public SIPLoad _child;
    
    public SIPLoad(){
        
    }
    
    public SIPLoad(String name, SIPLoad child){
        _name = name;
        _child = child;
    }
 
}
