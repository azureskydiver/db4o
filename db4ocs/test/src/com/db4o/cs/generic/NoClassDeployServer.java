/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.generic;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectServer;

public class NoClassDeployServer {
	public static void main(String[] args) {
		new File("testClient.yap").delete();
		ObjectServer server = Db4o.openServer("testClient.yap", 0x1111);
		server.grantAccess("db4o", "db4o");
	}	
}
