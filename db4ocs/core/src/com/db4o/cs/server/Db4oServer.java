package com.db4o.cs.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This is the main starting point for the db4o server.
 * <p/>
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 9:11:42 PM
 */
public class Db4oServer {
	public static int DEFAULT_PORT = 3246; // can you guess what this is?
	DefaultContext context = new DefaultContext();
	public static final String DEFAULT_FILE = "test.db4o";

	public static void main(String[] args) throws IOException {
		Db4oServer server = new Db4oServer();
		server.start();

	}

	public void grantAccess(String username, String password) {
		context.getAccessMap().put(username, password);
	}

	public void start() throws IOException {
		ServerSocket serverSocket = null;
		boolean listening = true;

		try {
			serverSocket = new ServerSocket(DEFAULT_PORT);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + DEFAULT_PORT);
			System.exit(-1);
		}

		while (listening) {
			new Db4oServerThread(context, serverSocket.accept()).start();
		}
		serverSocket.close();
	}
}
