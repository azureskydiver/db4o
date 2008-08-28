/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.ext.*;

/**
 * @decaf.ignore.jdk11
 */
public class TestListInMap {

	public Map map;
	
	public void storeOne() {
	    ExtObjectContainer db = Test.objectContainer();
		List list = db.collections().newLinkedList();
		list.add("ListEntry 1");
		db.store(list);
		map = db.collections().newHashMap(0); 			
		map.put("1", list);
	}
	
	public void testOne() {
	    List list = (List) map.get("1");
	    Object obj = list.get(0);
		System.out.println(obj);
	}
}

