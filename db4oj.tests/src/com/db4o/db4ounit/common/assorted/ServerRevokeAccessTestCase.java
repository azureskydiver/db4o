/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class ServerRevokeAccessTestCase implements TestCase {
	
	static final String FILE = "ServerRevokeAccessTest.yap";
	
	static final int SERVER_PORT = 0xdb42;
	
	static final String SERVER_HOSTNAME = "localhost";
	
	public void test() throws IOException {
		File4.delete(FILE);		
		ObjectServer server = Db4o.openServer(FILE, SERVER_PORT);
		try {	
			final String user = "hohohi";
			final String password = "hohoho";
			server.grantAccess(user, password);
		
			ObjectContainer con = Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, user, password);
			Assert.isNotNull(con);
			con.close();
			
			server.ext().revokeAccess(user);
			
			Assert.expect(Exception.class, new CodeBlock() {
				public void run() throws Exception {
					Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, user, password);
				}
			});
		} finally {
			server.close();
		}
	}	
}
