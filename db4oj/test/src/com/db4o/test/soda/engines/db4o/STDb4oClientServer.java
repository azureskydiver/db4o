/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.soda.engines.db4o;

import java.io.File;

import com.db4o.*;
import com.db4o.foundation.Cool;
import com.db4o.query.Query;
import com.db4o.test.soda.STEngine;

public class STDb4oClientServer implements STEngine {

	private static final int PORT = 4044;
	private static final String HOST = "localhost";
	private static final String FILE = "sodacs.yap";
	private static final String USER = "S.O.D.A.";
	private static final String PASS = "rocks";

	private static final boolean IN_PROCESS_SERVER = true;

	private com.db4o.ObjectServer server;
	private com.db4o.ObjectContainer con;

	/** 
	 * starts a db4o server.
	 * <br>To test with a remote server:<br>
	 * - set IN_PROCESS_SERVER to false
	 * - start STDb4oClientServer on the server
	 * - run SodaTest on the client
	 * - STDb4oClientServer needs to be uncommented in SodaTest#ENGINES
	 * The server can be stopped with CTRL + C.
	 */
	public static void main(String[] args) {
		new File(FILE).delete();
		ObjectServer server = Db4o.openServer(FILE, PORT);
		server.grantAccess(USER, PASS);
		server.ext().configure().messageLevel(-1);
	}

	public void reset() {
		new File(FILE).delete();
	}

	public Query query() {
		return con.query();
	}
	
	public void open() {
		Db4o.configure().messageLevel(-1);

		if (IN_PROCESS_SERVER) {
			server = Db4o.openServer(FILE, PORT);
			server.grantAccess(USER, PASS);
			// wait for the server to be online
			Cool.sleepWithoutInterruption(3000);
		}
		try {
			con = Db4o.openClient(HOST, PORT, USER, PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		con.close();
		if (IN_PROCESS_SERVER) {
			server.close();
		}
	}
	
	public void store(Object obj) {
		con.set(obj);
	}

	public void commit(){
		con.commit();
	}
	
	public void delete(Object obj){
		con.delete(obj);
	}
}
