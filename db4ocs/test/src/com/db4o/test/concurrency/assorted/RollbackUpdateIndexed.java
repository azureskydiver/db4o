/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency.assorted;

import com.db4o.config.*;
import com.db4o.cs.common.util.*;
import com.db4o.ext.*;
import com.db4o.test.persistent.*;

import db4ounit.*;

public class RollbackUpdateIndexed extends ClientServerTestCase {

	public void configure(Configuration config) {
		// FIXME: if _s or _i is indexed, the test fails
		config.objectClass(SimpleObject.class).objectField("_s").indexed(true);
		config.objectClass(SimpleObject.class).objectField("_i").indexed(true);
	}

	public void store(ExtObjectContainer oc) {
		oc.set(new SimpleObject("hello", 1));
	}
	
	/*
	 * set - rollback - commit - set - commit
	 */
	public void test() {
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = db();
		ExtObjectContainer oc3 = db();
		try {
			SimpleObject o1 = (SimpleObject) Db4oUtil.getOne(oc1,
					SimpleObject.class);
			o1.setS("o1");
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
			Assert.areEqual("hello", o2.getS());

			oc1.set(o1);
			oc1.commit();
			o2 = (SimpleObject) Db4oUtil.getOne(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("o1", o2.getS());
			
			SimpleObject o3 = (SimpleObject) Db4oUtil.getOne(oc3, SimpleObject.class);
			Assert.areEqual("o1", o3.getS());
		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
		}
	}
}
