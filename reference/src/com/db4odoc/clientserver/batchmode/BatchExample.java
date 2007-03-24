/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.clientserver.batchmode;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;

public class BatchExample {

	private static final String FILE = "test.yap";
	private static final int PORT= 0xdb40;
	private static final String USER = "db4o";
	private static final String PASS = "db4o";
	private static final String HOST = "localhost";
	
	private static final int NO_OF_OBJECTS = 1000;
	
	public static void main(String[] args) throws IOException {
		ObjectServer db4oServer = Db4o.openServer(FILE, PORT);
		try {
		    db4oServer.grantAccess(USER, PASS);
		    ObjectContainer container = Db4o.openClient(HOST, PORT, USER,
		    		PASS);
		    try {
		    	fillUpDb(container);
		    	container.ext().configure().clientServer().batchMessages(true);
		    	fillUpDb(container);
		    } finally {
		    	container.close();
		    }
		} finally {
			db4oServer.close();
		}
	}
	// end main
	

	private static void fillUpDb(ObjectContainer container) {
		System.out.println("Testing inserts");
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < NO_OF_OBJECTS; i++){
			Pilot pilot = new Pilot("pilot #"+ i, i);
			container.set(pilot);
		}
		long t2 = System.currentTimeMillis();
		long diff = t2 - t1;
		System.out.println("Operation time: " + diff + " ms.");
	}
	// end fillUpDb

}
