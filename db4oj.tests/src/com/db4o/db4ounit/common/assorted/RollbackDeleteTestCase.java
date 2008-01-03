/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.Db4oClientServerTestCase;

public class RollbackDeleteTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new RollbackDeleteTestCase().runClientServer();
	}

	protected void store() {
		store(new SimpleObject("hello", 1));
	}

	/*
	 * delete - rollback - delete - commit
	 */
	public void testDRDC() {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		ExtObjectContainer oc3 = openNewClient();
		try {
			SimpleObject o1 = (SimpleObject) retrieveOnlyInstance(oc1,
					SimpleObject.class);
			oc1.delete(o1);
			SimpleObject o2 = (SimpleObject) retrieveOnlyInstance(oc2,
					SimpleObject.class);
			Assert.areEqual("hello", o2.getS());

			oc1.rollback();

			o2 = (SimpleObject) retrieveOnlyInstance(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.commit();
			o2 = (SimpleObject) retrieveOnlyInstance(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.delete(o1);
			oc1.commit();

			assertOccurrences(oc3, SimpleObject.class, 0);
			assertOccurrences(oc2, SimpleObject.class, 0);

		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
		}
	}

	/*
	 * set - rollback - delete - commit
	 */
	public void testSRDC() {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		ExtObjectContainer oc3 = openNewClient();
		try {
			SimpleObject o1 = (SimpleObject) retrieveOnlyInstance(oc1,
					SimpleObject.class);
			oc1.store(o1);
			SimpleObject o2 = (SimpleObject) retrieveOnlyInstance(oc2,
					SimpleObject.class);
			Assert.areEqual("hello", o2.getS());

			oc1.rollback();

			o2 = (SimpleObject) retrieveOnlyInstance(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.commit();
			o2 = (SimpleObject) retrieveOnlyInstance(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.delete(o1);
			oc1.commit();

			assertOccurrences(oc3, SimpleObject.class, 0);
			assertOccurrences(oc2, SimpleObject.class, 0);

		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
		}
	}

}
