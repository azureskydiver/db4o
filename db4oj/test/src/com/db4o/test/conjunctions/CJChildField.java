/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.conjunctions;


public class CJChildField {
    
    public CJChild child;
    
    public int _id;
    
    public CJChildField(){
        
    }
    
    public CJChildField(int id){
        _id = id;
        child = new CJChild(_id);
    }
    
    public void store(){
        
        for (int i = 0; i < 20; i++) {
            
            
        }
        
    }
    
    
    
    
    
    
    
    
    

}
