/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.tuning;

import com.db4o.test.*;


public class TuningIdentity {
    
    static final int COUNT = 1000;
    
    TIMember member;
    
    public TuningIdentity(){
    }
    
    public TuningIdentity(TIMember member){
        this.member = member;
    }
    
    public void store(){
        for (int i = 0; i < COUNT; i++) {
            Test.store(new TuningIdentity(new TIMember("" + i)));
        }
    }
    
    public void test(){
        
    }
    
    public static class TIMember{
        
        String name;
        
        public TIMember(){
            
        }
        
        public TIMember(String name){
            this.name = name;
        }
        
    }
    

}
