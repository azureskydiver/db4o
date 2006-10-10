/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.regression;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.test.persistent.SimpleObject;

import db4ounit.Assert;

public class Case1207Test {
	
	/*
	 *  client 1: set and commit
	 *  client 2: set and rollback
	 */
	public void test1207() throws Exception {
		new File("case1207.yap").delete();
		ObjectServer server = Db4o.openServer("case1207.yap", 1207);
		server.grantAccess("db4o", "db4o");
		ObjectContainer oc1 = Db4o
				.openClient("127.0.0.1", 1207, "db4o", "db4o");
		ObjectContainer oc2 = Db4o
				.openClient("127.0.0.1", 1207, "db4o", "db4o");
		ObjectContainer oc3 = Db4o
				.openClient("127.0.0.1", 1207, "db4o", "db4o");
		try {
			for (int i = 0; i < 1000; i++) {
				SimpleObject c1 = new SimpleObject("oc " + i, i);
				SimpleObject c2 = new SimpleObject("oc2 " + i, i);
				oc1.set(c1);
				oc2.set(c2);
				oc2.rollback();
				c2 = new SimpleObject("oc2.2 " + i, i);
				oc2.set(c2);
			}
			oc1.commit();
			oc2.rollback();
			Assert.areEqual(1000, oc1.query(SimpleObject.class).size());
			Assert.areEqual(1000, oc2.query(SimpleObject.class).size());
			Assert.areEqual(1000, oc3.query(SimpleObject.class).size());
			System.out.println("GREEN!");
		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
			server.close();
			new File("case1207.yap").delete();
		}
	}

	public static void main(String[] args) throws Exception {
		new Case1207Test().test1207();
	}
}
