/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.types.*;

/**
 * @decaf.ignore.jdk11
 */
public class HashMapClearUnsaved {
    
    Map _map;
    
    public void storeOne(){
        _map = Test.objectContainer().collections().newHashMap(1);
        ((Db4oMap)_map).deleteRemoved(true);
        _map.put("myKey", "myValue");
        _map.clear();
    }
    
    public void testOne(){
        ((Db4oMap)_map).deleteRemoved(true);
        _map.clear();
        Test.ensure(_map.size() == 0);
    }

}
