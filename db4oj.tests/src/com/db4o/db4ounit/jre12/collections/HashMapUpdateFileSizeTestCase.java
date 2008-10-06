/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.io.*;
import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @decaf.ignore.jdk11
 */
public class HashMapUpdateFileSizeTestCase extends AbstractDb4oTestCase implements OptOutCS, OptOutDefragSolo, OptOutTA {

	public static void main(String[] args) {
		new HashMapUpdateFileSizeTestCase().runAll();
	}

	protected void store() throws Exception {
		HashMap map = new HashMap();
		fillMap(map);
		store(map);
	}

	private void fillMap(HashMap map) {
		map.put(new Integer(1), "string 1");
		map.put(new Integer(2), "String 2");
	}

	public void _testFileSize() throws Exception {
		warmUp();
		assertFileSizeConstant();
	}

	private void assertFileSizeConstant() throws Exception {
		defragment();
		long beforeUpdate = dbSize();
		for (int i = 0; i < 15; ++i) {
			updateMap();
		}
		defragment();
		long afterUpdate = dbSize();
		/*
		 * FIXME: the database file size is uncertain? 
		 * We met similar problem before.
		 */
		Assert.isTrue(afterUpdate - beforeUpdate < 2);
	}

	private void warmUp() throws Exception, IOException {
		for (int i = 0; i < 3; ++i) {
			updateMap();
		}
	}

	private void updateMap() throws Exception, IOException {
		HashMap map = (HashMap) retrieveOnlyInstance(HashMap.class);
		fillMap(map);
		store(map);
		db().commit();
	}
	
	private long dbSize() {
		return db().systemInfo().totalSize();
	}

}
