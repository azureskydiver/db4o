package com.db4o.cs.server;

import com.db4o.ObjectServer;
import com.db4o.ObjectContainer;
import com.db4o.ext.ExtObjectServer;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This is the main starting point for the db4o server.
 * <p/>
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 9:11:42 PM
 */
public class Db4oServer implements ObjectServer {
	public static final String DEFAULT_FILE = "test.db4o";
	public static int DEFAULT_PORT = 3246; // can you guess what this is?
	

	DefaultContext context;
	private ServerSocket serverSocket;
	private String file;
	private int port;

	public Db4oServer(String file, int port) {
		this.file = file;
		this.port = port;
		context = new DefaultContext(file, port);
	}

	public static void main(String[] args) throws IOException {
		Db4oServer server = new Db4oServer(DEFAULT_FILE, DEFAULT_PORT);
		server.start();
	}


	public ExtObjectServer ext() {
		return null;
	}

	public void grantAccess(String username, String password) {
		context.getAccessMap().put(username, password);
	}

	public ObjectContainer openClient() {
		return null;
	}

	public void start() throws IOException {
		boolean listening = true;

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Db4oServer open for business on " + port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			System.exit(-1);
		}

		while (listening) {
			try {
				new Db4oServerThread(context, serverSocket.accept()).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		serverSocket.close();
	}

	public boolean close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
