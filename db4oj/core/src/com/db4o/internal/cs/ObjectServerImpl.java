/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;

public class ObjectServerImpl implements ObjectServer, ExtObjectServer, Runnable,
		LoopbackSocketServer {
	
	private String i_name;

	private ServerSocket4 i_serverSocket;
	
	private int i_port;

	private int i_threadIDGen = 1;

	private Collection4 i_threads = new Collection4();

	private LocalObjectContainer i_yapFile;

	private final Object _lock=new Object();
	
	private Config4Impl _config;
	
	public ObjectServerImpl(final LocalObjectContainer yapFile, int port) {
		i_yapFile = yapFile;
		i_port = port;
		_config = i_yapFile.configImpl();
		i_name = "db4o ServerSocket FILE: " + yapFile.toString() + "  PORT:"+ i_port;
		
		i_yapFile.setServer(true);	
		
		configureObjectServer();

		ensureLoadStaticClass();
		ensureLoadConfiguredClasses();

		startupServerSocket();
	}

	private void startupServerSocket() {
		if (i_port <= 0) {
			return;
		}
		try {
			i_serverSocket = new ServerSocket4(i_port);
			i_serverSocket.setSoTimeout(_config.timeoutServerSocket());
		} catch (IOException e) {
			Exceptions4.throwRuntimeException(30, "" + i_port);
		}

		new Thread(this).start();
		// TODO: Carl, shouldn't this be a daemon?
		// Not sure Klaus, let's discuss.

		synchronized (_lock) {
			try {
				_lock.wait(1000);
				// Give the thread some time to get up.
				// We will get notified.
			} catch (Exception e) {
			}
		}
	}

	private void ensureLoadStaticClass() {
		i_yapFile.produceYapClass(i_yapFile.i_handlers.ICLASS_STATICCLASS);
	}
	
	private void ensureLoadConfiguredClasses() {
		// make sure all configured YapClasses are up in the repository
		_config.exceptionalClasses().forEachValue(new Visitor4() {
			public void visit(Object a_object) {
				i_yapFile.produceYapClass(i_yapFile.reflector().forName(
						((Config4Class) a_object).getName()));
			}
		});
	}

	private void configureObjectServer() {
		_config.callbacks(false);
		_config.isServer(true);
		// the minium activation depth of com.db4o.User.class should be 1.
		// Otherwise, we may get null password.
		_config.objectClass(User.class).minimumActivationDepth(1);
	}

	public void backup(String path) throws IOException {
		i_yapFile.backup(path);
	}

	final void checkClosed() {
		if (i_yapFile == null) {
			Exceptions4.throwRuntimeException(20, i_name);
		}
		i_yapFile.checkClosed();
	}

	public boolean close() {
		synchronized (Global4.lock) {
			// Take it easy.
			// Test cases hit close while communication
			// is still in progress.
			Cool.sleepIgnoringInterruption(100);
			try {
				if (i_serverSocket != null) {
					i_serverSocket.close();
				}
			} catch (Exception e) {
				if (Deploy.debug) {
					System.out
							.println("YapServer.close() ServerSocket failed to close.");
				}
			}
			i_serverSocket = null;
			boolean isClosed = i_yapFile == null ? true : i_yapFile.close();
			synchronized (i_threads) {
				Iterator4 i = new Collection4(i_threads).iterator();
				while (i.moveNext()) {
					((ServerMessageDispatcher) i.current()).close();
				}
			}
			i_yapFile = null;
			return isClosed;
		}
	}

	public Configuration configure() {
		return _config;
	}

	public ExtObjectServer ext() {
		return this;
	}

	ServerMessageDispatcher findThread(int a_threadID) {
		synchronized (i_threads) {
			Iterator4 i = i_threads.iterator();
			while (i.moveNext()) {
				ServerMessageDispatcher serverThread = (ServerMessageDispatcher) i.current();
				if (serverThread.i_threadID == a_threadID) {
					return serverThread;
				}
			}
		}
		return null;
	}

	public void grantAccess(String userName, String password) {
		synchronized (i_yapFile.i_lock) {
			checkClosed();
			i_yapFile.showInternalClasses(true);
			try {
				User existing = getUser(userName);
				if (existing != null) {
					setPassword(existing, password);
				} else {
					addUser(userName, password);
				}
				i_yapFile.commit();
			} finally {
				i_yapFile.showInternalClasses(false);
			}
		}
	}

	private void addUser(String userName, String password) {
		i_yapFile.set(new User(userName, password));
	}

	private void setPassword(User existing, String password) {
		existing.password = password;
		i_yapFile.set(existing);
	}

	User getUser(String userName) {
		final ObjectSet result = queryUsers(userName);
		if (!result.hasNext()) {
			return null;
		}
		return (User) result.next();
	}

	private ObjectSet queryUsers(String userName) {
		return i_yapFile.get(new User(userName, null));
	}

	public ObjectContainer objectContainer() {
		return i_yapFile;
	}

	public ObjectContainer openClient() {
		return openClient(Db4o.cloneConfiguration());
	}

	public ObjectContainer openClient(Configuration config) {
		checkClosed();
		try {
			ClientObjectContainer client = new ClientObjectContainer(config, openClientSocket(),
					Const4.EMBEDDED_CLIENT_USER + (i_threadIDGen - 1), "",
					false);
			client.blockSize(i_yapFile.blockSize());
			return client;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public LoopbackSocket openClientSocket() {
		int timeout = _config.timeoutClientSocket();
		LoopbackSocket clientFake = new LoopbackSocket(this, timeout);
		LoopbackSocket serverFake = new LoopbackSocket(this, timeout, clientFake);
		try {
			ServerMessageDispatcher thread = new ServerMessageDispatcher(this, i_yapFile,
					serverFake, i_threadIDGen++, true);
			synchronized (i_threads) {
				i_threads.add(thread);
			}
			thread.start();
			return clientFake;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	void removeThread(ServerMessageDispatcher aThread) {
		synchronized (i_threads) {
			i_threads.remove(aThread);
		}
	}

	public void revokeAccess(String userName) {
		synchronized (i_yapFile.i_lock) {
			i_yapFile.showInternalClasses(true);
			try {
				checkClosed();
				deleteUsers(userName);
				i_yapFile.commit();
			} finally {
				i_yapFile.showInternalClasses(false);
			}
		}
	}

	private void deleteUsers(String userName) {
		ObjectSet set = queryUsers(userName);
		while (set.hasNext()) {
			i_yapFile.delete(set.next());
		}
	}

	public void run() {
		Thread.currentThread().setName(i_name);
		i_yapFile.logMsg(31, "" + i_serverSocket.getLocalPort());
		synchronized (_lock) {
			_lock.notifyAll();
		}
		while (i_serverSocket != null) {
			try {
				ServerMessageDispatcher thread = new ServerMessageDispatcher(this, i_yapFile,
						i_serverSocket.accept(), i_threadIDGen++, false);
				synchronized (i_threads) {
					i_threads.add(thread);
				}
				thread.start();
			} catch (Exception e) {
			}
		}
	}
}
