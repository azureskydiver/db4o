/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	internal class YapServer : com.db4o.ObjectServer, com.db4o.ext.ExtObjectServer, j4o.lang.Runnable
	{
		private string i_name;

		private com.db4o.YapServerSocket i_serverSocket;

		private int i_threadIDGen = 1;

		private com.db4o.Collection4 i_threads = new com.db4o.Collection4();

		private com.db4o.YapFile i_yapFile;

		internal YapServer(com.db4o.YapFile a_yapFile, int a_port)
		{
			a_yapFile.setServer(true);
			i_name = "db4o ServerSocket  FILE: " + a_yapFile.ToString() + "  PORT:" + a_port;
			i_yapFile = a_yapFile;
			com.db4o.Config4Impl config = (com.db4o.Config4Impl)i_yapFile.configure();
			config.callbacks(false);
			config.i_isServer = true;
			a_yapFile.getYapClass(a_yapFile.i_handlers.ICLASS_STATICCLASS, true);
			config.i_exceptionalClasses.forEachValue(new _AnonymousInnerClass31(this, a_yapFile
				));
			if (config.i_messageLevel == 0)
			{
				config.i_messageLevel = 1;
			}
			if (a_port > 0)
			{
				try
				{
					i_serverSocket = new com.db4o.YapServerSocket(a_port);
					i_serverSocket.setSoTimeout(config.i_timeoutServerSocket);
				}
				catch (j4o.io.IOException e)
				{
					com.db4o.Db4o.throwRuntimeException(30, "" + a_port);
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

		private sealed class _AnonymousInnerClass31 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass31(YapServer _enclosing, com.db4o.YapFile a_yapFile)
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
				com.db4o.Db4o.throwRuntimeException(20, i_name);
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
					com.db4o.Iterator4 i = i_threads.iterator();
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
				com.db4o.Iterator4 i = i_threads.iterator();
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
				return new com.db4o.YapClient(openFakeClientSocket(), com.db4o.YapConst.EMBEDDED_CLIENT_USER
					 + (i_threadIDGen - 1), "", false);
			}
			catch (j4o.io.IOException e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return null;
		}

		internal virtual com.db4o.YapSocketFake openFakeClientSocket()
		{
			com.db4o.YapSocketFake clientFake = new com.db4o.YapSocketFake(this);
			com.db4o.YapSocketFake serverFake = new com.db4o.YapSocketFake(this, clientFake);
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
