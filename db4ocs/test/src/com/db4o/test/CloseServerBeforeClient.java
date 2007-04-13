/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;

import db4ounit.extensions.*;

public class CloseServerBeforeClient extends AbstractDb4oTestCase {
	
	public static void main(String[] arguments) {
		new CloseServerBeforeClient().runConcurrency();
	}
	
	public void test() throws Exception {
		new File("csbc.yap").delete();
		try {
			ObjectServer server = Db4o.openServer(
					"CloseServerBeforeClient.yap", 1207);
			server.grantAccess("db4o", "db4o");
			ObjectContainer oc = Db4o.openClient("127.0.0.1", 1207, "db4o",
					"db4o");
			server.close();
		} finally {
			new File("csbc.yap").delete();
		}
	}
}
