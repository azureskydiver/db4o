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
	
	private final String _name;

	private ServerSocket4 _serverSocket;
	
	private final int _port;

	private int i_threadIDGen = 1;

	private final Collection4 _threads = new Collection4();

	private LocalObjectContainer _container;

	private final Object _startupLock=new Object();
	
	private Config4Impl _config;
	
	public ObjectServerImpl(final LocalObjectContainer container, int port) {
		_container = container;
		_port = port;
		_config = _container.configImpl();
		_name = "db4o ServerSocket FILE: " + container.toString() + "  PORT:"+ _port;
		
		_container.setServer(true);	
		
		configureObjectServer();

		ensureLoadStaticClass();
		ensureLoadConfiguredClasses();

		startServer();
	}

	private void startServer() {
		if (isEmbeddedServer()) {
			return;
		}
		
		startServerSocket();
		startServerThread();
		waitForThreadStart();
	}

	private void startServerThread() {
		final Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	private void startServerSocket() {
		try {
			_serverSocket = new ServerSocket4(_port);
			_serverSocket.setSoTimeout(_config.timeoutServerSocket());
		} catch (IOException e) {
			Exceptions4.throwRuntimeException(Messages.COULD_NOT_OPEN_PORT, "" + _port);
		}
	}

	private boolean isEmbeddedServer() {
		return _port <= 0;
	}

	private void waitForThreadStart() {
		synchronized (_startupLock) {
			try {
				_startupLock.wait(1000);
				// Give the thread some time to get up.
				// We will get notified.
			} catch (Exception e) {
			}
		}
	}

	private void ensureLoadStaticClass() {
		_container.produceYapClass(_container.i_handlers.ICLASS_STATICCLASS);
	}
	
	private void ensureLoadConfiguredClasses() {
		// make sure all configured YapClasses are up in the repository
		_config.exceptionalClasses().forEachValue(new Visitor4() {
			public void visit(Object a_object) {
				_container.produceYapClass(_container.reflector().forName(
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
		_container.backup(path);
	}

	final void checkClosed() {
		if (_container == null) {
			Exceptions4.throwRuntimeException(Messages.CLOSED_OR_OPEN_FAILED, _name);
		}
		_container.checkClosed();
	}

	public synchronized boolean close() {
		closeServerSocket();
		boolean isClosed = closeFile();
		closeMessageDispatchers();
		return isClosed;
	}

	private boolean closeFile() {
		if (_container == null) {
			return true;
		}
		boolean isClosed = _container.close();
		_container = null;
		return isClosed;
	}

	private void closeMessageDispatchers() {
		Iterator4 i = iterateThreads();
		while (i.moveNext()) {
			try {
				((ServerMessageDispatcher) i.current()).close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Iterator4 iterateThreads() {
		synchronized (_threads) {
			return new Collection4(_threads).iterator();
		}
	}

	private void closeServerSocket() {
		try {
			if (_serverSocket != null) {
				_serverSocket.close();
			}
		} catch (Exception e) {
			if (Deploy.debug) {
				System.out
						.println("YapServer.close() ServerSocket failed to close.");
			}
		}
		_serverSocket = null;
	}

	public Configuration configure() {
		return _config;
	}

	public ExtObjectServer ext() {
		return this;
	}

	ServerMessageDispatcher findThread(int a_threadID) {
		synchronized (_threads) {
			Iterator4 i = _threads.iterator();
			while (i.moveNext()) {
				ServerMessageDispatcher serverThread = (ServerMessageDispatcher) i.current();
				if (serverThread.i_threadID == a_threadID) {
					return serverThread;
				}
			}
		}
		return null;
	}

	public synchronized void grantAccess(String userName, String password) {
		checkClosed();
		synchronized (_container.i_lock) {
			User existing = getUser(userName);
			if (existing != null) {
				setPassword(existing, password);
			} else {
				addUser(userName, password);
			}
			_container.commit();
		}
	}

	private void addUser(String userName, String password) {
		_container.set(new User(userName, password));
	}

	private void setPassword(User existing, String password) {
		existing.password = password;
		_container.set(existing);
	}

	User getUser(String userName) {
		final ObjectSet result = queryUsers(userName);
		if (!result.hasNext()) {
			return null;
		}
		return (User) result.next();
	}

	private ObjectSet queryUsers(String userName) {
		_container.showInternalClasses(true);
		try {
			return _container.get(new User(userName, null));
		} finally {
			_container.showInternalClasses(false);
		}
	}

	public ObjectContainer objectContainer() {
		return _container;
	}

	public ObjectContainer openClient() {
		return openClient(Db4o.cloneConfiguration());
	}

	public synchronized ObjectContainer openClient(Configuration config) {
		checkClosed();
		try {
			ClientObjectContainer client = new ClientObjectContainer(config, openClientSocket(),
					Const4.EMBEDDED_CLIENT_USER + (i_threadIDGen - 1), "",
					false);
			client.blockSize(_container.blockSize());
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
			ServerMessageDispatcher thread = new ServerMessageDispatcher(this, _container,
					serverFake, newThreadId(), true);
			addThread(thread);
			thread.start();
			return clientFake;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	void removeThread(ServerMessageDispatcher aThread) {
		synchronized (_threads) {
			_threads.remove(aThread);
		}
	}

	public synchronized void revokeAccess(String userName) {
		checkClosed();
		synchronized (_container.i_lock) {
			deleteUsers(userName);
			_container.commit();
		}
	}

	private void deleteUsers(String userName) {
		ObjectSet set = queryUsers(userName);
		while (set.hasNext()) {
			_container.delete(set.next());
		}
	}

	public void run() {
		setThreadName();
		logListeningOnPort();
		notifyThreadStarted();
		socketServerLoop();
	}

	private void setThreadName() {
		Thread.currentThread().setName(_name);
	}

	private void socketServerLoop() {
		while (_serverSocket != null) {
			try {
				ServerMessageDispatcher thread = new ServerMessageDispatcher(this, _container,
						_serverSocket.accept(), newThreadId(), false);
				addThread(thread);
				thread.start();
			} catch (Exception e) {
			}
		}
	}

	private void notifyThreadStarted() {
		synchronized (_startupLock) {
			_startupLock.notifyAll();
		}
	}

	private void logListeningOnPort() {
		_container.logMsg(Messages.SERVER_LISTENING_ON_PORT, "" + _serverSocket.getLocalPort());
	}

	private int newThreadId() {
		return i_threadIDGen++;
	}

	private void addThread(ServerMessageDispatcher thread) {
		synchronized (_threads) {
			_threads.add(thread);
		}
	}
}
