/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.TestConfigure;
import com.db4o.test.persistent.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class CascadeDeleteArray extends ClientServerTestCase {

	private SimpleObject[] elements;

	private int TOTAL_COUNT = TestConfigure.CONCURRENCY_THREAD_COUNT;

	public void store(ExtObjectContainer oc) {
		elements = new SimpleObject[TOTAL_COUNT];
		for (int i = 0; i < TOTAL_COUNT; ++i) {
			elements[i] = new SimpleObject("testString" + i, i);
		}
		oc.set(this);
	}

	protected void configure(Configuration config){
		config.objectClass(this).cascadeOnDelete(true);
	}
	
	public void test() {
		int total = 10;
		ExtObjectContainer[] ocs = new ExtObjectContainer[total];
		ObjectSet[] oss = new ObjectSet[total];
		for (int i = 0; i < total; i++) {
			ocs[i] = db();
			oss[i] = ocs[i].query(SimpleObject.class);
			Assert.areEqual(TOTAL_COUNT, oss[i].size());
		}
		for (int i = 0; i < total; i++) {
			Db4oUtil.deleteObjectSet(ocs[i], oss[i]);
		}
		ExtObjectContainer oc = db();
		try {
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, TOTAL_COUNT);
			// ocs[0] deletes all SimpleObject
			ocs[0].close();
			// FIXME: the following assertion fails
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
			for (int i = 1; i < total; i++) {
				ocs[i].close();
			}
			Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
		} finally {
			oc.close();
			for(int i = 0; i < total; i++) {
				if(!ocs[i].isClosed()){
					ocs[i].close();
				}
			}
		}
	}

	public void concDelete(ExtObjectContainer oc) throws Exception {
		int size = Db4oUtil.occurrences(oc, SimpleObject.class);
		if (size == 0) { // already deleted
			return;
		}
		ObjectSet os = oc.query(CascadeDeleteArray.class);
		if(os.size() == 0) { // already deteled
			return; 
		}
		Assert.areEqual(1, os.size());
		CascadeDeleteArray cda = (CascadeDeleteArray) os.next();
		Assert.areEqual(TOTAL_COUNT, size);
		
		// waits for other threads
		Thread.sleep(500);
		oc.delete(cda);
		// FIXME: the following assertion fails
		Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
	}

	public void checkDelete(ExtObjectContainer oc) {
		Db4oUtil.assertOccurrences(oc, SimpleObject.class, 0);
	}

}
