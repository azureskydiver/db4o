/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.regression;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

import db4ounit.*;

public class COR52TestCase implements TestCase {
	
	public static void main(String[] args) {
		new TestRunner(COR52TestCase.class).run();
	}
	
	private static final String TEST_FILE = Path4.getTempFileName();
	
	public void test() throws Exception {
		int originalActivationDepth = ((Config4Impl) Db4o.configure())
				.activationDepth();
		Db4o.configure().activationDepth(0);
		ObjectServer server = Db4o.openServer(TEST_FILE, -1);
		try {
			server.grantAccess("db4o", "db4o");
			ObjectContainer oc = Db4o.openClient("localhost", server.ext().port(), "db4o",
					"db4o");
			oc.close();
		} finally {
			Db4o.configure().activationDepth(originalActivationDepth);
			server.close();
			new File(TEST_FILE).delete();
		}

	}
}
