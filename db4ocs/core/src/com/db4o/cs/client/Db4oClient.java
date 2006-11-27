package com.db4o.cs.client;

import com.db4o.cs.client.protocol.ClientProtocol;
import com.db4o.cs.client.protocol.protocol1.Protocol1Client;
import com.db4o.cs.client.query.ClientQuery;
import com.db4o.cs.common.ObjectContainer2;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.query.Predicate;
import com.db4o.query.QueryComparator;
import com.db4o.ext.ExtObjectContainer;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;
import java.util.Comparator;
import java.util.List;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 12:19:41 AM
 */
public class Db4oClient implements ObjectContainer2 {
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
		this(host, DEFAULT_PORT);
	}

	public Db4oClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Initiate connection to the server.
	 *
	 * @throws IOException
	 */
	public void connect() throws IOException {
		//System.out.println("connecting...");
		if (host == null) {
			throw new UnknownHostException("Host not specified.  Please call setHost(String) to set.");
		}

		socket = new Socket(host, port);
		// test this with BufferedStream wrappers to see if it makes a difference
		out = socket.getOutputStream();
		in = socket.getInputStream();

		protocol = //new ObjectStreamProtocolClient(out, in);
				new Protocol1Client(out, in);
		protocol.writeHeaders();
	}


	/**
	 * Maybe this should just be part of connect, ie: connect(username, password)
	 *
	 * @param username
	 * @param password
	 */
	public boolean login(String username, String password) throws IOException {
		//System.out.println("Logging in...");
		return protocol.login(username, password);
	}

	/**
	 * Persist an object.
	 * @param o object to persist
	 * @throws IOException
	 */
	public void set(Object o) {
		try {
			protocol.set(o);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Send commit command to the server.
	 *
	 * @throws IOException
	 */
	public void commit() {
		try {
			protocol.commit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void deactivate(Object obj, int depth) {

	}

	public void activate(Object obj, int depth) {

	}

	/**
	 * Close the connection. No further operations will succeed.
	 *
	 * @throws IOException
	 */
	public boolean close() {
		commit(); // to keep the same as current behaviour
		try {
			protocol.close();
			out.close();
			in.close();
			socket.close();
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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


	public void delete(Object o) {
		try {
			protocol.delete(o);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ExtObjectContainer ext() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public Query query() {
		return new ClientQuery(this);
	}

	public <TargetType> ObjectSet<TargetType> query(Predicate<TargetType> predicate, Comparator<TargetType> comparator) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public void rollback() {

	}


	public ObjectSet query(Class aClass) {
		try {
			return new ObjectSetListWrapper(protocol.query(aClass));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ObjectSet query(Predicate predicate, QueryComparator comparator) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public ObjectSet query(Predicate predicate) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public ObjectSet get(Object template) {
		throw new UnsupportedOperationException("Not yet...");
	}

	/**
	 * This should be added to ObjectContainer interface
	 * @param query
	 * @return
	 */
	public List execute(Query query) throws IOException, ClassNotFoundException {
		return protocol.execute(query);
	}

}
