package com.db4o.cs.performance;

import com.db4o.Db4o;
import com.db4o.ObjectServer;

import java.io.File;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 2:06:35 AM
 */
public class OldServerRunner {
	private ObjectServer server;
	public static final int PORT = 3255;
	private static final String DB_FILE = "test.old.db4o";

	public static void main(String[] args) {
		OldServerRunner oldServerRunner = new OldServerRunner();
		oldServerRunner.start();
	}

	public void start() {
		File f= new File(DB_FILE);
		f.delete();
		server = Db4o.openServer(DB_FILE, PORT);
		server.grantAccess("test", "test");
	}
	public void close(){
		server.close();
	}
}
