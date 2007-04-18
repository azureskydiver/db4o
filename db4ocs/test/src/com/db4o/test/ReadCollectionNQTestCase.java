/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.test.persistent.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ReadCollectionNQTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new ReadCollectionNQTestCase().runConcurrency();
	}
	
	private static String testString = "simple test string";
	
	private static int LIST_SIZE = 100;
	
	private List list = new ArrayList();

	protected void store() throws Exception {
		for (int i = 0; i < LIST_SIZE; i++) {
			SimpleObject o = new SimpleObject(testString + i, i);
			list.add(o);
		}
		store(list);
	}

	public void concReadCollection(ExtObjectContainer oc) throws Exception {
		ObjectSet result = oc.query(new Predicate<List>() {
			public boolean match(List list) {
				return list.size() == LIST_SIZE;
			}
		});
		Assert.areEqual(1, result.size());
		List resultList = (List) result.next();
		Assert.areEqual(list, resultList);
	}
}
