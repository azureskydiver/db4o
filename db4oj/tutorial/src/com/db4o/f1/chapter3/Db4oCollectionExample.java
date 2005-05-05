/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.f1.chapter3;

import java.util.List;
import java.util.Map;

import com.db4o.ObjectContainer;

public class Db4oCollectionExample {
    private List myList;

    private static final int INITIAL_SIZE = 10;
    private Map myMap;
    
    private ObjectContainer database;
    
    /**
     * @return Returns the myList.
     */
    public List getMyList() {
        if (myList == null) {
            myList = database.ext().collections().newLinkedList();
        }
        return myList;
    }
    
    /**
     * @return Returns the myMap.
     */
    public Map getMyMap() {
        if (myMap == null) {
            myMap = database.ext()
                    .collections().newHashMap(INITIAL_SIZE);
        }
        return myMap;
    }
}
