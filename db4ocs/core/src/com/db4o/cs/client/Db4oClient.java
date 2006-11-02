package com.db4o.cs.client;

import com.db4o.cs.client.protocol.ClientProtocol;
import com.db4o.cs.client.protocol.objectStream.ObjectStreamProtocolClient;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;
import java.util.List;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 12:19:41 AM
 */
public class Db4oClient {
	public static int DEFAULT_PORT = 3246;

	private String host;
	private int port = DEFAULT_PORT;

	Socket socket = null;
	/*
	When the streams were referenced here and in the Protocol, it was hanging on new ObjectStreamProtocolClient(in, out) call.  strange.
	OutputStream out = null;
	InputStream in = null;*/
	private ClientProtocol protocol;
	private OutputStream out;
	private InputStream in;

	public Db4oClient(String host) {
		this.host = host;
	}

	/**
	 * Initiate connection to the server.
	 *
	 * @throws IOException
	 */
	public void connect() throws IOException {
		System.out.println("connecting...");
		if (host == null) {
			throw new UnknownHostException("Host not specified.  Please call setHost(String) to set.");
		}

		socket = new Socket(host, port);
		// test this with BufferedStream wrappers to see if it makes a difference
		out = socket.getOutputStream();
		in = socket.getInputStream();

		protocol = new ObjectStreamProtocolClient(out, in);
		protocol.writeHeaders();
	}


	/**
	 * Maybe this should just be part of connect, ie: connect(username, password)
	 *
	 * @param username
	 * @param password
	 */
	public boolean login(String username, String password) throws IOException {
		System.out.println("Logging in...");
		return protocol.login(username, password);
	}

	/**
	 * Persist an object.
	 * @param o object to persist
	 * @throws IOException
	 */
	public void set(Object o) throws IOException {
		protocol.set(o);
	}

	/**
	 * Send commit command to the server.
	 *
	 * @throws IOException
	 */
	public void commit() throws IOException {
		protocol.commit();
	}

	public List query(Class aClass) throws IOException, ClassNotFoundException {
		return protocol.query(aClass);
	}
	/**
	 * Close the connection. No further operations will succeed.
	 *
	 * @throws IOException
	 */
	public void close() throws IOException {
		commit(); // to keep the same as current behaviour
		protocol.close();
		out.close();
		in.close();
		socket.close();
	}


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


}
