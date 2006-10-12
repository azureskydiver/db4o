/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;

import db4ounit.extensions.ClientServerTestCase;

public class CloseServerBeforeClient extends ClientServerTestCase {
	
	public static void main(String[] arguments) {
		new CloseServerBeforeClient().runClientServer();
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
