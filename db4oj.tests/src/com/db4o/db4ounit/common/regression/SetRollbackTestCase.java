/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.regression;

import com.db4o.db4ounit.common.assorted.SimpleObject;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.Db4oClientServerTestCase;

public class SetRollbackTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new SetRollbackTestCase().runClientServer();
	}
	
	/*
	 * regression test: http://developer.db4o.com/forums/thread/29298.aspx
	 */
	public void testSetRollback() {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		try {
			for (int i = 0; i < 1000; i++) {
				SimpleObject obj1 = new SimpleObject("oc " + i, i);
				SimpleObject obj2 = new SimpleObject("oc2 " + i, i);
				oc1.set(obj1);
				oc2.set(obj2);
				oc2.rollback();
				obj2 = new SimpleObject("oc2.2 " + i, i);
				oc2.set(obj2);
			}
			oc1.commit();
			oc2.rollback();
			Assert.areEqual(1000, oc1.query(SimpleObject.class).size());
			Assert.areEqual(1000, oc2.query(SimpleObject.class).size());
		} finally {
			oc1.close();
			oc2.close();
		}
	}

}
