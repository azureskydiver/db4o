/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.legacy;

import java.util.*;

import com.db4o.test.*;


public class Db4oHashMapDeletedKey {

    public Map _map;
    
    public void storeOne(){
        _map = Test.objectContainer().collections().newHashMap(1);
        // _map = Test.objectContainer().collections().newIdentityHashMap(1);
        _map.put(new DHMDKey("key"), "value");
    }
    
    public void testOne(){
        DHMDKey key = (DHMDKey) Test.getOne(new DHMDKey(null));
        Test.delete(key);
        Test.defragment();
    }
    
    public static class DHMDKey{
        
        public String _name;
        
        public DHMDKey(String name){
            _name = name;
        }
        
        public int hashCode() {
            return _name.hashCode();
        }
        
    }

}
