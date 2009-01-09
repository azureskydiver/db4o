/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.types.*;

/**
 * 
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class StringInLists {

    public List arrayList;
    public List db4oLinkedList;
    public Map  hashMap;
    public Map  db4oHashMap;

    public void storeOne() {

        ExtObjectContainer oc = Test.objectContainer();
        Db4oCollections col = oc.collections();

        arrayList = new ArrayList();
        fillList(arrayList);
        
        db4oLinkedList = col.newLinkedList();
        fillList(db4oLinkedList);
        
        hashMap = new HashMap();
        fillMap(hashMap);
        
        db4oHashMap = col.newHashMap(1);
        fillMap(db4oHashMap);
    }

    public void testOne() {
        checkList(arrayList);
        checkList(db4oLinkedList);
        checkMap(hashMap);
        checkMap(db4oHashMap);
    }

    private void fillList(List list) {
        list.add("One");
        list.add("Two");
        list.add("Three");
    }

    private void fillMap(Map map) {
        map.put("One", "One");
        map.put("Two", "Two");
        map.put("Three", "Three");
    }

    private void checkList(List list) {
        Test.ensure(list.size() == 3);
        Test.ensure(list.get(0).equals("One"));
        Test.ensure(list.get(1).equals("Two"));
        Test.ensure(list.get(2).equals("Three"));
    }
    
    private void checkMap(Map map){
        Test.ensure(map.size() == 3);
        Test.ensure(map.get("One").equals("One"));
        Test.ensure(map.get("Two").equals("Two"));
        Test.ensure(map.get("Three").equals("Three"));
    }

}