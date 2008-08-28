/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

/**
 * @decaf.ignore.jdk11
 */
public class PrimitivesInCollection {
	
	List list;

	public void storeOne()
	{
		list = Test.objectContainer().collections().newLinkedList();
		list.add(new Integer(1));
		list.add("hi");
	}

	public void testOne()
	{
		Test.ensure(list.contains(new Integer(1)));
		Test.ensure(list.contains("hi"));
	}



}
