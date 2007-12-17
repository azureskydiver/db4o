/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.ssl;

import com.db4o.Db4o;
import com.db4o.ObjectServer;
import java.net.ssl.*;

public class SSLServer {

	  private static String   HOST = "localhost";  
	  private static String   FILE = "reference.db4o";
	  private static int    PORT = 0xdb40;
	  private static String   USER = "db4o";
	  private static String   PASS = "db4o";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Configuration config = Db4o.newConfiguration();
		 ObjectServer db4oServer = Db4o.openServer(config, FILE, PORT, );
	     db4oServer.grantAccess(USER, PASS);
	     

	}

}
