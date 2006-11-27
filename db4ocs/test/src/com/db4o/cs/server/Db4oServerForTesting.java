package com.db4o.cs.server;

import com.db4o.cs.performance.OldVsNew;

import java.io.IOException;
import java.io.File;

/**
 * This should be run in a separate process before running ClientServerTest
 *
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 12:54:02 AM
 */
public class Db4oServerForTesting {
	public static final int PORT = OldVsNew.PORT;

	public static void main(String[] args) throws IOException {
		File f= new File(Db4oServer.DEFAULT_FILE);
		f.delete();

		Db4oServer server = new Db4oServer(Db4oServer.DEFAULT_FILE, PORT);
		server.start();
		server.grantAccess("test", "test");
	}
}
