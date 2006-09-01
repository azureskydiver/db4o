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
			a_yapFile.SetServer(true);
			i_name = "db4o ServerSocket  FILE: " + a_yapFile.ToString() + "  PORT:" + a_port;
			i_yapFile = a_yapFile;
			com.db4o.Config4Impl config = (com.db4o.Config4Impl)i_yapFile.Configure();
			config.Callbacks(false);
			config.IsServer(true);
			a_yapFile.GetYapClass(a_yapFile.i_handlers.ICLASS_STATICCLASS, true);
			config.ExceptionalClasses().ForEachValue(new _AnonymousInnerClass33(this, a_yapFile
				));
			if (config.MessageLevel() == 0)
			{
				config.MessageLevel(1);
			}
			if (a_port > 0)
			{
				try
				{
					i_serverSocket = new com.db4o.foundation.network.YapServerSocket(a_port);
					i_serverSocket.SetSoTimeout(config.TimeoutServerSocket());
				}
				catch (System.IO.IOException e)
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(30, "" + a_port);
				}
				new j4o.lang.Thread(this).Start();
				lock (this)
				{
					try
					{
						j4o.lang.JavaSystem.Wait(this, 1000);
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

			public void Visit(object a_object)
			{
				a_yapFile.GetYapClass(a_yapFile.Reflector().ForName(((com.db4o.Config4Class)a_object
					).GetName()), true);
			}

			private readonly YapServer _enclosing;

			private readonly com.db4o.YapFile a_yapFile;
		}

		public virtual void Backup(string path)
		{
			i_yapFile.Backup(path);
		}

		internal void CheckClosed()
		{
			if (i_yapFile == null)
			{
				com.db4o.inside.Exceptions4.ThrowRuntimeException(20, i_name);
			}
			i_yapFile.CheckClosed();
		}

		public virtual bool Close()
		{
			lock (com.db4o.Db4o.Lock)
			{
				com.db4o.foundation.Cool.SleepIgnoringInterruption(100);
				try
				{
					if (i_serverSocket != null)
					{
						i_serverSocket.Close();
					}
				}
				catch (System.Exception e)
				{
				}
				i_serverSocket = null;
				bool isClosed = i_yapFile == null ? true : i_yapFile.Close();
				lock (i_threads)
				{
					com.db4o.foundation.Iterator4 i = i_threads.Iterator();
					while (i.MoveNext())
					{
						((com.db4o.YapServerThread)i.Current()).Close();
					}
				}
				i_yapFile = null;
				return isClosed;
			}
		}

		public virtual com.db4o.config.Configuration Configure()
		{
			return i_yapFile.Configure();
		}

		public virtual com.db4o.ext.ExtObjectServer Ext()
		{
			return this;
		}

		internal virtual com.db4o.YapServerThread FindThread(int a_threadID)
		{
			lock (i_threads)
			{
				com.db4o.foundation.Iterator4 i = i_threads.Iterator();
				while (i.MoveNext())
				{
					com.db4o.YapServerThread serverThread = (com.db4o.YapServerThread)i.Current();
					if (serverThread.i_threadID == a_threadID)
					{
						return serverThread;
					}
				}
			}
			return null;
		}

		public virtual void GrantAccess(string userName, string password)
		{
			lock (i_yapFile.i_lock)
			{
				CheckClosed();
				com.db4o.User user = new com.db4o.User();
				user.name = userName;
				i_yapFile.ShowInternalClasses(true);
				com.db4o.User existing = (com.db4o.User)i_yapFile.Get(user).Next();
				if (existing != null)
				{
					existing.password = password;
					i_yapFile.Set(existing);
				}
				else
				{
					user.password = password;
					i_yapFile.Set(user);
				}
				i_yapFile.Commit();
				i_yapFile.ShowInternalClasses(false);
			}
		}

		public virtual com.db4o.ObjectContainer ObjectContainer()
		{
			return i_yapFile;
		}

		public virtual com.db4o.ObjectContainer OpenClient()
		{
			try
			{
				com.db4o.YapClient client = new com.db4o.YapClient(OpenClientSocket(), com.db4o.YapConst
					.EMBEDDED_CLIENT_USER + (i_threadIDGen - 1), "", false);
				client.BlockSize(i_yapFile.BlockSize());
				return client;
			}
			catch (System.IO.IOException e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
			}
			return null;
		}

		public virtual com.db4o.foundation.network.YapSocketFake OpenClientSocket()
		{
			int timeout = ((com.db4o.Config4Impl)Configure()).TimeoutClientSocket();
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
					i_threads.Add(thread);
				}
				thread.Start();
				return clientFake;
			}
			catch (System.Exception e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
			}
			return null;
		}

		internal virtual void RemoveThread(com.db4o.YapServerThread aThread)
		{
			lock (i_threads)
			{
				i_threads.Remove(aThread);
			}
		}

		public virtual void RevokeAccess(string userName)
		{
			lock (i_yapFile.i_lock)
			{
				i_yapFile.ShowInternalClasses(true);
				CheckClosed();
				com.db4o.User user = new com.db4o.User();
				user.name = userName;
				com.db4o.ObjectSet set = i_yapFile.Get(user);
				while (set.HasNext())
				{
					i_yapFile.Delete(set.Next());
				}
				i_yapFile.Commit();
				i_yapFile.ShowInternalClasses(false);
			}
		}

		public virtual void Run()
		{
			j4o.lang.Thread.CurrentThread().SetName(i_name);
			i_yapFile.LogMsg(31, "" + i_serverSocket.GetLocalPort());
			lock (this)
			{
				j4o.lang.JavaSystem.Notify(this);
			}
			while (i_serverSocket != null)
			{
				try
				{
					com.db4o.YapServerThread thread = new com.db4o.YapServerThread(this, i_yapFile, i_serverSocket
						.Accept(), i_threadIDGen++, false);
					lock (i_threads)
					{
						i_threads.Add(thread);
					}
					thread.Start();
				}
				catch (System.Exception e)
				{
				}
			}
		}
	}
}
