/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.inside.*;

class YapServer implements ObjectServer, ExtObjectServer, Runnable,
		YapSocketFakeServer {
	private String i_name;

	private YapServerSocket i_serverSocket;

	private int i_threadIDGen = 1;

	private Collection4 i_threads = new Collection4();

	private YapFile i_yapFile;

	YapServer(final YapFile a_yapFile, int a_port) {
		a_yapFile.setServer(true);
		i_name = "db4o ServerSocket  FILE: " + a_yapFile.toString() + "  PORT:"
				+ a_port;
		i_yapFile = a_yapFile;
		Config4Impl config = (Config4Impl) i_yapFile.configure();
		config.callbacks(false);
		config.isServer(true);

		a_yapFile.getYapClass(a_yapFile.i_handlers.ICLASS_STATICCLASS, true);

		// make sure all configured YapClasses are up in the repository
		config.exceptionalClasses().forEachValue(new Visitor4() {
			public void visit(Object a_object) {
				a_yapFile.getYapClass(a_yapFile.reflector().forName(
						((Config4Class) a_object).getName()), true);
			}
		});

		if (a_port > 0) {
			try {
				i_serverSocket = new YapServerSocket(a_port);
				i_serverSocket.setSoTimeout(config.timeoutServerSocket());
			} catch (IOException e) {
				Exceptions4.throwRuntimeException(30, "" + a_port);
			}

			new Thread(this).start();
			// TODO: Carl, shouldn't this be a daemon?
			// Not sure Klaus, let's discuss.

			synchronized (this) {
				try {
					wait(1000);
					// Give the thread some time to get up.
					// We will get notified.
				} catch (Exception e) {
				}
			}
		}
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
		synchronized (Db4o.lock) {
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
					((YapServerThread) i.current()).close();
				}
			}
			i_yapFile = null;
			return isClosed;
		}
	}

	public Configuration configure() {
		return i_yapFile.configure();
	}

	public ExtObjectServer ext() {
		return this;
	}

	YapServerThread findThread(int a_threadID) {
		synchronized (i_threads) {
			Iterator4 i = i_threads.iterator();
			while (i.moveNext()) {
				YapServerThread serverThread = (YapServerThread) i.current();
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
			User user = new User();
			user.name = userName;
			i_yapFile.showInternalClasses(true);
			User existing = (User) i_yapFile.get(user).next();
			if (existing != null) {
				existing.password = password;
				i_yapFile.set(existing);
			} else {
				user.password = password;
				i_yapFile.set(user);
			}
			i_yapFile.commit();
			i_yapFile.showInternalClasses(false);
		}
	}

	public ObjectContainer objectContainer() {
		return i_yapFile;
	}

	public ObjectContainer openClient() {
		return openClient(Db4o.cloneConfiguration());
	}

	public ObjectContainer openClient(Configuration config) {
		try {
			YapClient client = new YapClient(config, openClientSocket(),
					YapConst.EMBEDDED_CLIENT_USER + (i_threadIDGen - 1), "",
					false);
			client.blockSize(i_yapFile.blockSize());
			return client;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public YapSocketFake openClientSocket() {
		int timeout = ((Config4Impl) configure()).timeoutClientSocket();
		YapSocketFake clientFake = new YapSocketFake(this, timeout);
		YapSocketFake serverFake = new YapSocketFake(this, timeout, clientFake);
		try {
			YapServerThread thread = new YapServerThread(this, i_yapFile,
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

	void removeThread(YapServerThread aThread) {
		synchronized (i_threads) {
			i_threads.remove(aThread);
		}
	}

	public void revokeAccess(String userName) {
		synchronized (i_yapFile.i_lock) {
			i_yapFile.showInternalClasses(true);
			checkClosed();
			User user = new User();
			user.name = userName;
			ObjectSet set = i_yapFile.get(user);
			while (set.hasNext()) {
				i_yapFile.delete(set.next());
			}
			i_yapFile.commit();
			i_yapFile.showInternalClasses(false);
		}
	}

	public void run() {
		Thread.currentThread().setName(i_name);
		i_yapFile.logMsg(31, "" + i_serverSocket.getLocalPort());
		synchronized (this) {
			this.notify();
		}
		while (i_serverSocket != null) {
			try {
				YapServerThread thread = new YapServerThread(this, i_yapFile,
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
