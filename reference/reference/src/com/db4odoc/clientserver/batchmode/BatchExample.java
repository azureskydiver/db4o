/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.clientserver.batchmode;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;

public class BatchExample {

	private static final String FILE = "reference.db4o";

	private static final int PORT = 0xdb40;

	private static final String USER = "db4o";

	private static final String PASS = "db4o";

	private static final String HOST = "localhost";

	private static final int NO_OF_OBJECTS = 1000;

	public static void main(String[] args) throws IOException {
		ObjectServer db4oServer = Db4o.openServer(FILE, PORT);
		try {
			db4oServer.grantAccess(USER, PASS);
			Configuration configuration = Db4o.newConfiguration();
			fillUpDb(configuration);
			configuration.clientServer().batchMessages(true);
			fillUpDb(configuration);
		} finally {
			db4oServer.close();
		}
	}

	// end main

	private static void fillUpDb(Configuration configuration) throws IOException {
		System.out.println("Testing inserts");
		ObjectContainer container = Db4o.openClient(HOST, PORT, USER,
				PASS);
		try {
			long t1 = System.currentTimeMillis();
			for (int i = 0; i < NO_OF_OBJECTS; i++) {
				Pilot pilot = new Pilot("pilot #" + i, i);
				container.store(pilot);
			}
			long t2 = System.currentTimeMillis();
			long diff = t2 - t1;
			System.out.println("Operation time: " + diff + " ms.");
		} finally {
			container.close();
		}
	}
	// end fillUpDb

}
