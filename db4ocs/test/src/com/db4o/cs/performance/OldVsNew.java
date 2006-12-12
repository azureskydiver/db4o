package com.db4o.cs.performance;

import com.db4o.ObjectContainer;
import com.db4o.Db4o;
import com.db4o.ObjectServer;
import com.db4o.cs.client.Db4oClient;
import com.db4o.cs.server.Db4oServer;

import java.io.IOException;
import java.io.File;

/**
 * User: treeder
 * Date: Nov 25, 2006
 * Time: 6:29:59 PM
 */
public class OldVsNew {
	private static final int MODE_OLD = 1;
	private static final int MODE_NEW = 2;
	public static int mode = MODE_NEW; // change to switch between old and new mode

	private static final String HOST =
//			 "localhost";
			"192.168.0.100";
	public static final int PORT = 11445;
	private static final String USER = "test";
	private static final String PASS = "test";
	private static final String YAP_FILE = "db4otest.yap";
	private static final String YAP_DIR = "./";

	protected static ObjectContainer openConnection() throws IOException {
		ObjectContainer oc;
		//long start = System.currentTimeMillis();
		if (mode == MODE_OLD) {
			oc = Db4o.openClient(HOST, PORT, USER, PASS);
		} else {
			Db4oClient client = new Db4oClient(HOST, PORT);
			client.connect();
			oc = client;
		}
		//long end = System.currentTimeMillis();
		//long duration = end - start;
		//System.out.println("open duration [" + getModeAsString(mode) + "]: " + duration);
		return oc;
	}

	private static String getModeAsString(int mode) {
		return mode == MODE_OLD ? "OLD" : "NEW";
	}

	protected static ObjectServer openServer() throws IOException {
		ObjectServer server;
		server = getObjectServerForFilename(YAP_FILE, PORT, true);
		server.grantAccess(USER, PASS);
		return server;
	}

	public static ObjectServer getObjectServerForFilename(String yapfilename, int port, boolean forceDelete) throws IOException {
		File parentDir = new File(YAP_DIR);
		File dbfile = new File(parentDir, yapfilename);
		if (forceDelete && dbfile.exists()) {
			System.out.println("deleting old db");
			dbfile.delete();
		}

		// basic config options ///////////////////////////
		Db4o.configure().exceptionsOnNotStorable(true);
		Db4o.configure().objectClass("java.math.BigDecimal").translate(new com.db4o.config.TSerializable());

		// for replication ////////////////////////////////
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		//Db4o.configure().setOut(new PrintStream(new StreamEater()));

		ObjectServer objectServer;
		if (mode == MODE_OLD) {
			objectServer = Db4o.openServer(dbfile.getPath(), port);
		} else {
			Db4oServer server = new Db4oServer(dbfile.getPath(), port);
			server.start();
			objectServer = server;
		}
		return objectServer;
	}
}
