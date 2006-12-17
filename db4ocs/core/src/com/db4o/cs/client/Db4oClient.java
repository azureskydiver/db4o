package com.db4o.cs.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.cs.client.batch.UpdateSet;
import com.db4o.cs.client.protocol.ClientProtocol;
import com.db4o.cs.client.protocol.protocol1.ObjectMarshaller;
import com.db4o.cs.client.protocol.protocol1.Protocol1Client;
import com.db4o.cs.client.query.ClientQuery;
import com.db4o.cs.common.ObjectContainer2;
import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.ObjectInfo;
import com.db4o.ext.StoredClass;
import com.db4o.ext.SystemInfo;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.replication.ReplicationConflictHandler;
import com.db4o.replication.ReplicationProcess;
import com.db4o.types.Db4oCollections;

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
	When the streams were referenced here and in the Protocol, it was hanging on new BaseClientProtocol(in, out) call.  strange.
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

		protocol = //new BaseClientProtocol(out, in);
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
	 *
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

	public void activate(Object obj, int depth) {

	}
	
	public void deactivate(Object obj, int depth) {

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
			pause();
			out.close();
			in.close();
			socket.close();
			System.out.println("writeObjectStopWatch count: " + ObjectMarshaller.stopWatchWriteObject.count() + " duration: " + ObjectMarshaller.stopWatchWriteObject.totalDuration() + " average: " + ObjectMarshaller.stopWatchWriteObject.average());
			ObjectMarshaller.stopWatchWriteObject.reset();
			System.out.println("gettingIdStopWatch count: " + Protocol1Client.stopWatchForSet.count() + " duration: " + Protocol1Client.stopWatchForSet.totalDuration() + " average: " + Protocol1Client.stopWatchForSet.average());
			Protocol1Client.stopWatchForSet.reset();
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void pause() {
		// for testing purposes
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
	 *
	 * @param query
	 * @return
	 */
	public List execute(Query query) throws IOException, ClassNotFoundException {
		return protocol.execute(query);
	}

	public void batch(UpdateSet updateSet, Query query) {
		try {
			protocol.batch(updateSet, query);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getByID(long id) {
		try {
			return protocol.getByID(id);
		} catch (IOException e) {
			return null;
		}
	}

	public long getID(Object obj) {
		try {
			return protocol.getID(obj);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public Configuration configure() {
		// doesn't do anthing
		// TODO: replace this stub method
		return null;
	}

	
	public void backup(String path) throws IOException {
		throw new UnsupportedOperationException("Not yet...");
		
	}

	public void bind(Object obj, long id) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public Db4oCollections collections() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public Object descend(Object obj, String[] path) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public ObjectInfo getObjectInfo(Object obj) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public Db4oDatabase identity() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public boolean isActive(Object obj) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public boolean isCached(long ID) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public boolean isClosed() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public boolean isStored(Object obj) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public ReflectClass[] knownClasses() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public Object lock() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public void migrateFrom(ObjectContainer objectContainer) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public <T> T peekPersisted(T object, int depth, boolean committed) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public void purge() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public void purge(Object obj) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public GenericReflector reflector() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public void refresh(Object obj, int depth) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public void releaseSemaphore(String name) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public ReplicationProcess replicationBegin(ObjectContainer peerB, ReplicationConflictHandler conflictHandler) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public void set(Object obj, int depth) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public boolean setSemaphore(String name, int waitForAvailability) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public StoredClass storedClass(Object clazz) {
		throw new UnsupportedOperationException("Not yet...");
	}

	public StoredClass[] storedClasses() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public SystemInfo systemInfo() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public long version() {
		throw new UnsupportedOperationException("Not yet...");
	}

	public <T> T getByUUID(Db4oUUID uuid) {
		throw new UnsupportedOperationException("Not yet...");
	}






}
