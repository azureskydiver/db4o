/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.test.persistent.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeDeleteArrayTestCase extends Db4oClientServerTestCase {
	public static void main(String[] args) {
		new CascadeDeleteArrayTestCase().runConcurrency();
	}

	public static class ArrayItem {
		public SimpleObject[] elements;
	}

	private int ELEMENT_COUNT = 10;

	public void store() {
		ArrayItem item = new ArrayItem();
		item.elements = new SimpleObject[ELEMENT_COUNT];
		for (int i = 0; i < ELEMENT_COUNT; ++i) {
			item.elements[i] = new SimpleObject("testString" + i, i);
		}
		store(item);
	}

	protected void configure(Configuration config) {
		config.objectClass(ArrayItem.class).cascadeOnDelete(true);
	}

	public void test() throws Exception {
		int total = 10;
		ExtObjectContainer[] containers = new ExtObjectContainer[total];
		ExtObjectContainer container = null;
		try {
			for (int i = 0; i < total; i++) {
				containers[i] = openNewClient();
				Assert.areEqual(ELEMENT_COUNT, countOccurences(containers[i],
						SimpleObject.class));
			}

			for (int i = 0; i < total; i++) {
				deleteAll(containers[i], SimpleObject.class);
			}

			container = openNewClient();

			assertOccurences(container, SimpleObject.class, ELEMENT_COUNT);
			// ocs[0] deletes all SimpleObject
			containers[0].commit();
			containers[0].close();
			// FIXME: the following assertion fails
			assertOccurences(container, SimpleObject.class, 0);
			for (int i = 1; i < total; i++) {
				containers[i].close();
			}
			assertOccurences(container, SimpleObject.class, 0);
		} finally {
			if(container != null) {
				container.close();
			}
			for (int i = 0; i < total; i++) {
				if (containers[i] != null) {
					containers[i].close();
				}
			}
		}
	}

	public void concDelete(ExtObjectContainer oc) throws Exception {
		int size = countOccurences(oc, SimpleObject.class);
		if (size == 0) { // already deleted
			return;
		}
		Assert.areEqual(ELEMENT_COUNT, size);

		ObjectSet os = oc.query(ArrayItem.class);
		if (os.size() == 0) { // already deteled
			return;
		}
		Assert.areEqual(1, os.size());
		ArrayItem item = (ArrayItem) os.next();

		// waits for other threads
		Thread.sleep(500);
		oc.delete(item);
		// FIXME: the following assertion fails
		assertOccurences(oc, SimpleObject.class, 0);
	}

	public void checkDelete(ExtObjectContainer oc) {
		assertOccurences(oc, SimpleObject.class, 0);
	}

}
