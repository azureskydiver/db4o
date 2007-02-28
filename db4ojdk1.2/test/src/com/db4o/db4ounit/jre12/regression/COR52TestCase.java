/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.regression;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.internal.Config4Impl;

import db4ounit.TestCase;

public class COR52TestCase implements TestCase {
	
	private static final String TEST_FILE = "COR52.db4o";
	
	public void test() throws Exception {
		int originalActivationDepth = ((Config4Impl) Db4o.configure())
				.activationDepth();
		Db4o.configure().activationDepth(0);
		ObjectServer server = Db4o.openServer(TEST_FILE, 1111);
		try {
			server.grantAccess("db4o", "db4o");
			ObjectContainer oc = Db4o.openClient("localhost", 1111, "db4o",
					"db4o");
			oc.close();
		} finally {
			Db4o.configure().activationDepth(originalActivationDepth);
			new File(TEST_FILE).delete();
			server.close();
		}

	}
}
