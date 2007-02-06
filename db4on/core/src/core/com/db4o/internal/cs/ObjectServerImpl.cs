namespace com.db4o.@internal.cs
{
	public class ObjectServerImpl : com.db4o.ObjectServer, com.db4o.ext.ExtObjectServer
		, j4o.lang.Runnable, com.db4o.foundation.network.LoopbackSocketServer
	{
		private string i_name;

		private com.db4o.foundation.network.ServerSocket4 i_serverSocket;

		private int i_threadIDGen = 1;

		private com.db4o.foundation.Collection4 i_threads = new com.db4o.foundation.Collection4
			();

		private com.db4o.@internal.LocalObjectContainer i_yapFile;

		private readonly object _lock = new object();

		public ObjectServerImpl(com.db4o.@internal.LocalObjectContainer a_yapFile, int a_port
			)
		{
			a_yapFile.SetServer(true);
			i_name = "db4o ServerSocket  FILE: " + a_yapFile.ToString() + "  PORT:" + a_port;
			i_yapFile = a_yapFile;
			com.db4o.@internal.Config4Impl config = (com.db4o.@internal.Config4Impl)i_yapFile
				.Configure();
			config.Callbacks(false);
			config.IsServer(true);
			a_yapFile.ProduceYapClass(a_yapFile.i_handlers.ICLASS_STATICCLASS);
			config.ExceptionalClasses().ForEachValue(new _AnonymousInnerClass41(this, a_yapFile
				));
			if (a_port > 0)
			{
				try
				{
					i_serverSocket = new com.db4o.foundation.network.ServerSocket4(a_port);
					i_serverSocket.SetSoTimeout(config.TimeoutServerSocket());
				}
				catch (System.IO.IOException)
				{
					com.db4o.@internal.Exceptions4.ThrowRuntimeException(30, string.Empty + a_port);
				}
				new j4o.lang.Thread(this).Start();
				lock (_lock)
				{
					try
					{
						j4o.lang.JavaSystem.Wait(_lock, 1000);
					}
					catch
					{
					}
				}
			}
		}

		private sealed class _AnonymousInnerClass41 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass41(ObjectServerImpl _enclosing, com.db4o.@internal.LocalObjectContainer
				 a_yapFile)
			{
				this._enclosing = _enclosing;
				this.a_yapFile = a_yapFile;
			}

			public void Visit(object a_object)
			{
				a_yapFile.ProduceYapClass(a_yapFile.Reflector().ForName(((com.db4o.@internal.Config4Class
					)a_object).GetName()));
			}

			private readonly ObjectServerImpl _enclosing;

			private readonly com.db4o.@internal.LocalObjectContainer a_yapFile;
		}

		public virtual void Backup(string path)
		{
			i_yapFile.Backup(path);
		}

		internal void CheckClosed()
		{
			if (i_yapFile == null)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(20, i_name);
			}
			i_yapFile.CheckClosed();
		}

		public virtual bool Close()
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				com.db4o.foundation.Cool.SleepIgnoringInterruption(100);
				try
				{
					if (i_serverSocket != null)
					{
						i_serverSocket.Close();
					}
				}
				catch
				{
				}
				i_serverSocket = null;
				bool isClosed = i_yapFile == null ? true : i_yapFile.Close();
				lock (i_threads)
				{
					System.Collections.IEnumerator i = new com.db4o.foundation.Collection4(i_threads)
						.GetEnumerator();
					while (i.MoveNext())
					{
						((com.db4o.@internal.cs.ServerMessageDispatcher)i.Current).Close();
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

		internal virtual com.db4o.@internal.cs.ServerMessageDispatcher FindThread(int a_threadID
			)
		{
			lock (i_threads)
			{
				System.Collections.IEnumerator i = i_threads.GetEnumerator();
				while (i.MoveNext())
				{
					com.db4o.@internal.cs.ServerMessageDispatcher serverThread = (com.db4o.@internal.cs.ServerMessageDispatcher
						)i.Current;
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
				i_yapFile.ShowInternalClasses(true);
				try
				{
					com.db4o.User existing = GetUser(userName);
					if (existing != null)
					{
						SetPassword(existing, password);
					}
					else
					{
						AddUser(userName, password);
					}
					i_yapFile.Commit();
				}
				finally
				{
					i_yapFile.ShowInternalClasses(false);
				}
			}
		}

		private void AddUser(string userName, string password)
		{
			i_yapFile.Set(new com.db4o.User(userName, password));
		}

		private void SetPassword(com.db4o.User existing, string password)
		{
			existing.password = password;
			i_yapFile.Set(existing);
		}

		internal virtual com.db4o.User GetUser(string userName)
		{
			com.db4o.ObjectSet result = QueryUsers(userName);
			if (!result.HasNext())
			{
				return null;
			}
			return (com.db4o.User)result.Next();
		}

		private com.db4o.ObjectSet QueryUsers(string userName)
		{
			return i_yapFile.Get(new com.db4o.User(userName, null));
		}

		public virtual com.db4o.ObjectContainer ObjectContainer()
		{
			return i_yapFile;
		}

		public virtual com.db4o.ObjectContainer OpenClient()
		{
			return OpenClient(com.db4o.Db4o.CloneConfiguration());
		}

		public virtual com.db4o.ObjectContainer OpenClient(com.db4o.config.Configuration 
			config)
		{
			CheckClosed();
			try
			{
				com.db4o.@internal.cs.ClientObjectContainer client = new com.db4o.@internal.cs.ClientObjectContainer
					(config, OpenClientSocket(), com.db4o.@internal.Const4.EMBEDDED_CLIENT_USER + (i_threadIDGen
					 - 1), string.Empty, false);
				client.BlockSize(i_yapFile.BlockSize());
				return client;
			}
			catch (System.IO.IOException e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
			}
			return null;
		}

		public virtual com.db4o.foundation.network.LoopbackSocket OpenClientSocket()
		{
			int timeout = ((com.db4o.@internal.Config4Impl)Configure()).TimeoutClientSocket();
			com.db4o.foundation.network.LoopbackSocket clientFake = new com.db4o.foundation.network.LoopbackSocket
				(this, timeout);
			com.db4o.foundation.network.LoopbackSocket serverFake = new com.db4o.foundation.network.LoopbackSocket
				(this, timeout, clientFake);
			try
			{
				com.db4o.@internal.cs.ServerMessageDispatcher thread = new com.db4o.@internal.cs.ServerMessageDispatcher
					(this, i_yapFile, serverFake, i_threadIDGen++, true);
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

		internal virtual void RemoveThread(com.db4o.@internal.cs.ServerMessageDispatcher 
			aThread)
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
				try
				{
					CheckClosed();
					DeleteUsers(userName);
					i_yapFile.Commit();
				}
				finally
				{
					i_yapFile.ShowInternalClasses(false);
				}
			}
		}

		private void DeleteUsers(string userName)
		{
			com.db4o.ObjectSet set = QueryUsers(userName);
			while (set.HasNext())
			{
				i_yapFile.Delete(set.Next());
			}
		}

		public virtual void Run()
		{
			j4o.lang.Thread.CurrentThread().SetName(i_name);
			i_yapFile.LogMsg(31, string.Empty + i_serverSocket.GetLocalPort());
			lock (_lock)
			{
				j4o.lang.JavaSystem.NotifyAll(_lock);
			}
			while (i_serverSocket != null)
			{
				try
				{
					com.db4o.@internal.cs.ServerMessageDispatcher thread = new com.db4o.@internal.cs.ServerMessageDispatcher
						(this, i_yapFile, i_serverSocket.Accept(), i_threadIDGen++, false);
					lock (i_threads)
					{
						i_threads.Add(thread);
					}
					thread.Start();
				}
				catch
				{
				}
			}
		}
	}
}
