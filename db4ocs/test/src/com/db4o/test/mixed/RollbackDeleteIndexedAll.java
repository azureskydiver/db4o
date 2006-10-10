/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.mixed;

import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.persistent.SimpleObject;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class RollbackDeleteIndexedAll extends ClientServerTestCase {

	public void configure(Configuration config) {
		config.objectClass(SimpleObject.class).objectField("_s").indexed(true);
		config.objectClass(SimpleObject.class).objectField("_i").indexed(true);
	}
	

	public void store(ExtObjectContainer oc) {
		oc.set(new SimpleObject("hello", 1));
	}

	/*
	 * delete - rollback - delete - commit
	 */
	public void testDRDC() {
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = db();
		ExtObjectContainer oc3 = db();
		try {
			SimpleObject o1 = (SimpleObject) Db4oUtil.getOne(oc1,
					SimpleObject.class);
			oc1.delete(o1);
			SimpleObject o2 = (SimpleObject) Db4oUtil.getOne(oc2,
					SimpleObject.class);
			Assert.areEqual("hello", o2.getS());

			oc1.rollback();

			o2 = (SimpleObject) Db4oUtil.getOne(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.commit();
			o2 = (SimpleObject) Db4oUtil.getOne(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.delete(o1);
			oc1.commit();

			// FIXME: the following assertion fails
			Db4oUtil.assertOccurrences(oc3, SimpleObject.class, 0);
			Db4oUtil.assertOccurrences(oc2, SimpleObject.class, 0);

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
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = db();
		ExtObjectContainer oc3 = db();
		try {
			SimpleObject o1 = (SimpleObject) Db4oUtil.getOne(oc1,
					SimpleObject.class);
			oc1.set(o1);
			SimpleObject o2 = (SimpleObject) Db4oUtil.getOne(oc2,
					SimpleObject.class);
			Assert.areEqual("hello", o2.getS());

			oc1.rollback();

			o2 = (SimpleObject) Db4oUtil.getOne(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.commit();
			o2 = (SimpleObject) Db4oUtil.getOne(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.delete(o1);
			oc1.commit();

			Db4oUtil.assertOccurrences(oc3, SimpleObject.class, 0);
			Db4oUtil.assertOccurrences(oc2, SimpleObject.class, 0);

		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
		}
	}

}
