/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.custom;

import java.util.*;

import db4ounit.extensions.*;


public class Db4oHashMapDeletedKeyTestCase extends AbstractDb4oTestCase {

	public static class Data {
		public Map _map;
	}
	
    protected void store(){
    	Data data=new Data();
        data._map = db().collections().newHashMap(1);
        // _map = Test.objectContainer().collections().newIdentityHashMap(1);
        data._map.put(new DHMDKey("key"), "value");
        store(data);
    }
    
    public void test() throws Exception{
        DHMDKey key = (DHMDKey) retrieveOnlyInstance(DHMDKey.class);
        db().delete(key);
        reopen();
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
