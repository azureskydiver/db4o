namespace com.db4o
{
	internal class YapServer : com.db4o.ObjectServer, com.db4o.ext.ExtObjectServer, j4o.lang.Runnable
		, com.db4o.foundation.network.YapSocketFakeServer
	{
		private string i_name;

		private com.db4o.foundation.network.YapServerSocket i_serverSocket;

		private int i_threadIDGen = 1;

		private com.db4o.foundation.Collection4 i_threads = new com.db4o.foundation.Collection4
			();

		private com.db4o.YapFile i_yapFile;

		internal YapServer(com.db4o.YapFile a_yapFile, int a_port)
		{
			a_yapFile.setServer(true);
			i_name = "db4o ServerSocket  FILE: " + a_yapFile.ToString() + "  PORT:" + a_port;
			i_yapFile = a_yapFile;
			com.db4o.Config4Impl config = (com.db4o.Config4Impl)i_yapFile.configure();
			config.callbacks(false);
			config.isServer(true);
			a_yapFile.getYapClass(a_yapFile.i_handlers.ICLASS_STATICCLASS, true);
			config.exceptionalClasses().forEachValue(new _AnonymousInnerClass33(this, a_yapFile
				));
			if (config.messageLevel() == 0)
			{
				config.messageLevel(1);
			}
			if (a_port > 0)
			{
				try
				{
					i_serverSocket = new com.db4o.foundation.network.YapServerSocket(a_port);
					i_serverSocket.setSoTimeout(config.timeoutServerSocket());
				}
				catch (System.IO.IOException e)
				{
					com.db4o.inside.Exceptions4.throwRuntimeException(30, "" + a_port);
				}
				new j4o.lang.Thread(this).start();
				lock (this)
				{
					try
					{
						j4o.lang.JavaSystem.wait(this, 1000);
					}
					catch (System.Exception e)
					{
					}
				}
			}
		}

		private sealed class _AnonymousInnerClass33 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass33(YapServer _enclosing, com.db4o.YapFile a_yapFile)
			{
				this._enclosing = _enclosing;
				this.a_yapFile = a_yapFile;
			}

			public void visit(object a_object)
			{
				a_yapFile.getYapClass(a_yapFile.reflector().forName(((com.db4o.Config4Class)a_object
					).getName()), true);
			}

			private readonly YapServer _enclosing;

			private readonly com.db4o.YapFile a_yapFile;
		}

		public virtual void backup(string path)
		{
			i_yapFile.backup(path);
		}

		internal void checkClosed()
		{
			if (i_yapFile == null)
			{
				com.db4o.inside.Exceptions4.throwRuntimeException(20, i_name);
			}
			i_yapFile.checkClosed();
		}

		public virtual bool close()
		{
			lock (com.db4o.Db4o.Lock)
			{
				com.db4o.foundation.Cool.sleepIgnoringInterruption(100);
				try
				{
					if (i_serverSocket != null)
					{
						i_serverSocket.close();
					}
				}
				catch (System.Exception e)
				{
				}
				i_serverSocket = null;
				bool isClosed = i_yapFile == null ? true : i_yapFile.close();
				lock (i_threads)
				{
					com.db4o.foundation.Iterator4 i = i_threads.iterator();
					while (i.hasNext())
					{
						((com.db4o.YapServerThread)i.next()).close();
					}
				}
				i_yapFile = null;
				return isClosed;
			}
		}

		public virtual com.db4o.config.Configuration configure()
		{
			return i_yapFile.configure();
		}

		public virtual com.db4o.ext.ExtObjectServer ext()
		{
			return this;
		}

		internal virtual com.db4o.YapServerThread findThread(int a_threadID)
		{
			lock (i_threads)
			{
				com.db4o.foundation.Iterator4 i = i_threads.iterator();
				while (i.hasNext())
				{
					com.db4o.YapServerThread serverThread = (com.db4o.YapServerThread)i.next();
					if (serverThread.i_threadID == a_threadID)
					{
						return serverThread;
					}
				}
			}
			return null;
		}

		public virtual void grantAccess(string userName, string password)
		{
			lock (i_yapFile.i_lock)
			{
				checkClosed();
				com.db4o.User user = new com.db4o.User();
				user.name = userName;
				i_yapFile.showInternalClasses(true);
				com.db4o.User existing = (com.db4o.User)i_yapFile.get(user).next();
				if (existing != null)
				{
					existing.password = password;
					i_yapFile.set(existing);
				}
				else
				{
					user.password = password;
					i_yapFile.set(user);
				}
				i_yapFile.commit();
				i_yapFile.showInternalClasses(false);
			}
		}

		public virtual com.db4o.ObjectContainer objectContainer()
		{
			return i_yapFile;
		}

		public virtual com.db4o.ObjectContainer openClient()
		{
			try
			{
				return new com.db4o.YapClient(openClientSocket(), com.db4o.YapConst.EMBEDDED_CLIENT_USER
					 + (i_threadIDGen - 1), "", false);
			}
			catch (System.IO.IOException e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return null;
		}

		public virtual com.db4o.foundation.network.YapSocketFake openClientSocket()
		{
			int timeout = ((com.db4o.Config4Impl)configure()).timeoutClientSocket();
			com.db4o.foundation.network.YapSocketFake clientFake = new com.db4o.foundation.network.YapSocketFake
				(this, timeout);
			com.db4o.foundation.network.YapSocketFake serverFake = new com.db4o.foundation.network.YapSocketFake
				(this, timeout, clientFake);
			try
			{
				com.db4o.YapServerThread thread = new com.db4o.YapServerThread(this, i_yapFile, serverFake
					, i_threadIDGen++, true);
				lock (i_threads)
				{
					i_threads.add(thread);
				}
				thread.start();
				return clientFake;
			}
			catch (System.Exception e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return null;
		}

		internal virtual void removeThread(com.db4o.YapServerThread aThread)
		{
			lock (i_threads)
			{
				i_threads.remove(aThread);
			}
		}

		public virtual void revokeAccess(string userName)
		{
			lock (i_yapFile.i_lock)
			{
				i_yapFile.showInternalClasses(true);
				checkClosed();
				com.db4o.User user = new com.db4o.User();
				user.name = userName;
				com.db4o.ObjectSet set = i_yapFile.get(user);
				while (set.hasNext())
				{
					i_yapFile.delete(set.next());
				}
				i_yapFile.commit();
				i_yapFile.showInternalClasses(false);
			}
		}

		public virtual void run()
		{
			j4o.lang.Thread.currentThread().setName(i_name);
			i_yapFile.logMsg(31, "" + i_serverSocket.getLocalPort());
			lock (this)
			{
				j4o.lang.JavaSystem.notify(this);
			}
			while (i_serverSocket != null)
			{
				try
				{
					com.db4o.YapServerThread thread = new com.db4o.YapServerThread(this, i_yapFile, i_serverSocket
						.accept(), i_threadIDGen++, false);
					lock (i_threads)
					{
						i_threads.add(thread);
					}
					thread.start();
				}
				catch (System.Exception e)
				{
				}
			}
		}
	}
}
