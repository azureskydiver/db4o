/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

/**
 * @decaf.ignore.jdk11
 */
public class SimplestDb4oList {
	
	List list;
	
	public void storeOne(){
		list = Test.objectContainer().collections().newLinkedList();
		list.add("hi");
	}
	
	public void testOne(){
		Test.ensure(list.get(0).equals("hi"));
	}

}
