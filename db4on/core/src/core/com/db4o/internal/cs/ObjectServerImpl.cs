namespace com.db4o.@internal.cs
{
	public class ObjectServerImpl : com.db4o.ObjectServer, com.db4o.ext.ExtObjectServer
		, j4o.lang.Runnable, com.db4o.foundation.network.LoopbackSocketServer
	{
		private readonly string _name;

		private com.db4o.foundation.network.ServerSocket4 _serverSocket;

		private readonly int _port;

		private int i_threadIDGen = 1;

		private readonly com.db4o.foundation.Collection4 _threads = new com.db4o.foundation.Collection4
			();

		private com.db4o.@internal.LocalObjectContainer _container;

		private readonly object _startupLock = new object();

		private com.db4o.@internal.Config4Impl _config;

		public ObjectServerImpl(com.db4o.@internal.LocalObjectContainer container, int port
			)
		{
			_container = container;
			_port = port;
			_config = _container.ConfigImpl();
			_name = "db4o ServerSocket FILE: " + container.ToString() + "  PORT:" + _port;
			_container.SetServer(true);
			ConfigureObjectServer();
			EnsureLoadStaticClass();
			EnsureLoadConfiguredClasses();
			StartServer();
		}

		private void StartServer()
		{
			if (IsEmbeddedServer())
			{
				return;
			}
			StartServerSocket();
			StartServerThread();
			WaitForThreadStart();
		}

		private void StartServerThread()
		{
			j4o.lang.Thread thread = new j4o.lang.Thread(this);
			thread.SetDaemon(true);
			thread.Start();
		}

		private void StartServerSocket()
		{
			try
			{
				_serverSocket = new com.db4o.foundation.network.ServerSocket4(_port);
				_serverSocket.SetSoTimeout(_config.TimeoutServerSocket());
			}
			catch (System.IO.IOException)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(com.db4o.@internal.Messages.
					COULD_NOT_OPEN_PORT, string.Empty + _port);
			}
		}

		private bool IsEmbeddedServer()
		{
			return _port <= 0;
		}

		private void WaitForThreadStart()
		{
			lock (_startupLock)
			{
				try
				{
					j4o.lang.JavaSystem.Wait(_startupLock, 1000);
				}
				catch
				{
				}
			}
		}

		private void EnsureLoadStaticClass()
		{
			_container.ProduceYapClass(_container.i_handlers.ICLASS_STATICCLASS);
		}

		private void EnsureLoadConfiguredClasses()
		{
			_config.ExceptionalClasses().ForEachValue(new _AnonymousInnerClass95(this));
		}

		private sealed class _AnonymousInnerClass95 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass95(ObjectServerImpl _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				this._enclosing._container.ProduceYapClass(this._enclosing._container.Reflector()
					.ForName(((com.db4o.@internal.Config4Class)a_object).GetName()));
			}

			private readonly ObjectServerImpl _enclosing;
		}

		private void ConfigureObjectServer()
		{
			_config.Callbacks(false);
			_config.IsServer(true);
			_config.ObjectClass(j4o.lang.JavaSystem.GetClassForType(typeof(com.db4o.User))).MinimumActivationDepth
				(1);
		}

		public virtual void Backup(string path)
		{
			_container.Backup(path);
		}

		internal void CheckClosed()
		{
			if (_container == null)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(com.db4o.@internal.Messages.
					CLOSED_OR_OPEN_FAILED, _name);
			}
			_container.CheckClosed();
		}

		public virtual bool Close()
		{
			lock (this)
			{
				CloseServerSocket();
				bool isClosed = CloseFile();
				CloseMessageDispatchers();
				return isClosed;
			}
		}

		private bool CloseFile()
		{
			if (_container == null)
			{
				return true;
			}
			bool isClosed = _container.Close();
			_container = null;
			return isClosed;
		}

		private void CloseMessageDispatchers()
		{
			System.Collections.IEnumerator i = IterateThreads();
			while (i.MoveNext())
			{
				try
				{
					((com.db4o.@internal.cs.ServerMessageDispatcher)i.Current).Close();
				}
				catch (System.Exception e)
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
				}
			}
		}

		private System.Collections.IEnumerator IterateThreads()
		{
			lock (_threads)
			{
				return new com.db4o.foundation.Collection4(_threads).GetEnumerator();
			}
		}

		private void CloseServerSocket()
		{
			try
			{
				if (_serverSocket != null)
				{
					_serverSocket.Close();
				}
			}
			catch
			{
			}
			_serverSocket = null;
		}

		public virtual com.db4o.config.Configuration Configure()
		{
			return _config;
		}

		public virtual com.db4o.ext.ExtObjectServer Ext()
		{
			return this;
		}

		internal virtual com.db4o.@internal.cs.ServerMessageDispatcher FindThread(int a_threadID
			)
		{
			lock (_threads)
			{
				System.Collections.IEnumerator i = _threads.GetEnumerator();
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
			lock (this)
			{
				CheckClosed();
				lock (_container.i_lock)
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
					_container.Commit();
				}
			}
		}

		private void AddUser(string userName, string password)
		{
			_container.Set(new com.db4o.User(userName, password));
		}

		private void SetPassword(com.db4o.User existing, string password)
		{
			existing.password = password;
			_container.Set(existing);
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
			_container.ShowInternalClasses(true);
			try
			{
				return _container.Get(new com.db4o.User(userName, null));
			}
			finally
			{
				_container.ShowInternalClasses(false);
			}
		}

		public virtual com.db4o.ObjectContainer ObjectContainer()
		{
			return _container;
		}

		public virtual com.db4o.ObjectContainer OpenClient()
		{
			return OpenClient(com.db4o.Db4o.CloneConfiguration());
		}

		public virtual com.db4o.ObjectContainer OpenClient(com.db4o.config.Configuration 
			config)
		{
			lock (this)
			{
				CheckClosed();
				try
				{
					com.db4o.@internal.cs.ClientObjectContainer client = new com.db4o.@internal.cs.ClientObjectContainer
						(config, OpenClientSocket(), com.db4o.@internal.Const4.EMBEDDED_CLIENT_USER + (i_threadIDGen
						 - 1), string.Empty, false);
					client.BlockSize(_container.BlockSize());
					return client;
				}
				catch (System.IO.IOException e)
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
				}
				return null;
			}
		}

		public virtual com.db4o.foundation.network.LoopbackSocket OpenClientSocket()
		{
			int timeout = _config.TimeoutClientSocket();
			com.db4o.foundation.network.LoopbackSocket clientFake = new com.db4o.foundation.network.LoopbackSocket
				(this, timeout);
			com.db4o.foundation.network.LoopbackSocket serverFake = new com.db4o.foundation.network.LoopbackSocket
				(this, timeout, clientFake);
			try
			{
				com.db4o.@internal.cs.ServerMessageDispatcher thread = new com.db4o.@internal.cs.ServerMessageDispatcher
					(this, _container, serverFake, NewThreadId(), true);
				AddThread(thread);
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
			lock (_threads)
			{
				_threads.Remove(aThread);
			}
		}

		public virtual void RevokeAccess(string userName)
		{
			lock (this)
			{
				CheckClosed();
				lock (_container.i_lock)
				{
					DeleteUsers(userName);
					_container.Commit();
				}
			}
		}

		private void DeleteUsers(string userName)
		{
			com.db4o.ObjectSet set = QueryUsers(userName);
			while (set.HasNext())
			{
				_container.Delete(set.Next());
			}
		}

		public virtual void Run()
		{
			SetThreadName();
			LogListeningOnPort();
			NotifyThreadStarted();
			SocketServerLoop();
		}

		private void SetThreadName()
		{
			j4o.lang.Thread.CurrentThread().SetName(_name);
		}

		private void SocketServerLoop()
		{
			while (_serverSocket != null)
			{
				try
				{
					com.db4o.@internal.cs.ServerMessageDispatcher thread = new com.db4o.@internal.cs.ServerMessageDispatcher
						(this, _container, _serverSocket.Accept(), NewThreadId(), false);
					AddThread(thread);
					thread.Start();
				}
				catch
				{
				}
			}
		}

		private void NotifyThreadStarted()
		{
			lock (_startupLock)
			{
				j4o.lang.JavaSystem.NotifyAll(_startupLock);
			}
		}

		private void LogListeningOnPort()
		{
			_container.LogMsg(com.db4o.@internal.Messages.SERVER_LISTENING_ON_PORT, string.Empty
				 + _serverSocket.GetLocalPort());
		}

		private int NewThreadId()
		{
			return i_threadIDGen++;
		}

		private void AddThread(com.db4o.@internal.cs.ServerMessageDispatcher thread)
		{
			lock (_threads)
			{
				_threads.Add(thread);
			}
		}
	}
}
