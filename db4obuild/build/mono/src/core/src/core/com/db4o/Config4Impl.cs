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
	/// <summary>Configuration template for creating new db4o files</summary>
	/// <exclude></exclude>
	public sealed class Config4Impl : com.db4o.config.Configuration, j4o.lang.Cloneable
		, com.db4o.DeepClone, com.db4o.messaging.MessageSender
	{
		internal int i_activationDepth = 5;

		internal bool i_automaticShutDown = true;

		internal byte i_blockSize = 1;

		internal string i_blobPath;

		internal bool i_callbacks = true;

		internal int i_callConstructors;

		internal bool i_classActivationDepthConfigurable = true;

		internal j4o.lang.ClassLoader i_classLoader;

		internal bool i_detectSchemaChanges = true;

		internal bool i_disableCommitRecovery;

		internal int i_discardFreeSpace;

		internal byte i_encoding = com.db4o.YapConst.UNICODE;

		internal bool i_encrypt;

		internal com.db4o.Hashtable4 i_exceptionalClasses = new com.db4o.Hashtable4(16);

		internal bool i_exceptionsOnNotStorable;

		public int i_generateUUIDs;

		public int i_generateVersionNumbers;

		internal bool i_isServer = false;

		internal bool i_lockFile = true;

		internal int i_messageLevel = com.db4o.YapConst.NONE;

		internal com.db4o.messaging.MessageRecipient i_messageRecipient;

		internal com.db4o.messaging.MessageSender i_messageSender;

		internal j4o.io.PrintStream i_outStream;

		internal string i_password;

		internal bool i_readonly;

		private com.db4o.reflect.Reflector _configuredReflector;

		private com.db4o.reflect.generic.GenericReflector _reflector;

		internal com.db4o.Collection4 i_rename;

		internal int i_reservedStorageSpace;

		internal bool i_singleThreadedClient;

		internal com.db4o.YapStream i_stream;

		internal bool i_testConstructors = true;

		internal int i_timeoutClientSocket = com.db4o.YapConst.CLIENT_SOCKET_TIMEOUT;

		internal int i_timeoutPingClients = com.db4o.YapConst.CONNECTION_TIMEOUT;

		internal int i_timeoutServerSocket = com.db4o.YapConst.SERVER_SOCKET_TIMEOUT;

		internal int i_updateDepth;

		internal int i_weakReferenceCollectionInterval = 1000;

		internal bool i_weakReferences = true;

		internal com.db4o.io.IoAdapter i_ioAdapter = new com.db4o.io.RandomAccessFileAdapter
			();

		internal int activationDepth()
		{
			return i_activationDepth;
		}

		public void activationDepth(int depth)
		{
			i_activationDepth = depth;
		}

		public void automaticShutDown(bool flag)
		{
			i_automaticShutDown = flag;
		}

		public void blockSize(int bytes)
		{
			if (bytes < 1 || bytes > 127)
			{
				com.db4o.Db4o.throwRuntimeException(1);
			}
			if (i_stream != null)
			{
				com.db4o.Db4o.throwRuntimeException(46);
			}
			i_blockSize = (byte)bytes;
		}

		public void callbacks(bool turnOn)
		{
			i_callbacks = turnOn;
		}

		public void callConstructors(bool flag)
		{
			i_callConstructors = flag ? com.db4o.YapConst.YES : com.db4o.YapConst.NO;
		}

		public void classActivationDepthConfigurable(bool turnOn)
		{
			i_classActivationDepthConfigurable = turnOn;
		}

		internal com.db4o.Config4Class configClass(string className)
		{
			com.db4o.Config4Class config = (com.db4o.Config4Class)i_exceptionalClasses.get(className
				);
			return config;
		}

		public object deepClone(object param)
		{
			com.db4o.Config4Impl ret = null;
			try
			{
				ret = (com.db4o.Config4Impl)j4o.lang.JavaSystem.clone(this);
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
			}
			ret.i_stream = (com.db4o.YapStream)param;
			if (i_exceptionalClasses != null)
			{
				ret.i_exceptionalClasses = (com.db4o.Hashtable4)i_exceptionalClasses.deepClone(ret
					);
			}
			if (i_rename != null)
			{
				ret.i_rename = (com.db4o.Collection4)i_rename.deepClone(ret);
			}
			if (_reflector != null)
			{
				ret._reflector = (com.db4o.reflect.generic.GenericReflector)_reflector.deepClone(
					ret);
			}
			return ret;
		}

		public void detectSchemaChanges(bool flag)
		{
			i_detectSchemaChanges = flag;
		}

		public void disableCommitRecovery()
		{
			i_disableCommitRecovery = true;
		}

		public void discardFreeSpace(int bytes)
		{
			i_discardFreeSpace = bytes;
		}

		public void encrypt(bool flag)
		{
			globalSettingOnly();
			i_encrypt = flag;
		}

		internal void ensureDirExists(string path)
		{
			j4o.io.File file = new j4o.io.File(path);
			if (!file.exists())
			{
				file.mkdirs();
			}
			if (file.exists() && file.isDirectory())
			{
			}
			else
			{
				throw new j4o.io.IOException(com.db4o.Messages.get(37, path));
			}
		}

		internal j4o.io.PrintStream errStream()
		{
			return i_outStream == null ? j4o.lang.JavaSystem.err : i_outStream;
		}

		public void exceptionsOnNotStorable(bool flag)
		{
			i_exceptionsOnNotStorable = flag;
		}

		public void generateUUIDs(int setting)
		{
			i_generateUUIDs = setting;
			storeStreamBootRecord();
		}

		private void storeStreamBootRecord()
		{
			if (i_stream is com.db4o.YapFile)
			{
				com.db4o.YapFile yapFile = (com.db4o.YapFile)i_stream;
				yapFile.i_bootRecord.initConfig(this);
				yapFile.setInternal(yapFile.i_systemTrans, yapFile.i_bootRecord, false);
				yapFile.i_systemTrans.commit();
			}
		}

		public void generateVersionNumbers(int setting)
		{
			i_generateVersionNumbers = setting;
			storeStreamBootRecord();
		}

		public com.db4o.messaging.MessageSender getMessageSender()
		{
			return this;
		}

		private void globalSettingOnly()
		{
			if (i_stream != null)
			{
				j4o.lang.JavaSystem.printStackTrace(new System.Exception());
				com.db4o.Db4o.throwRuntimeException(46);
			}
		}

		public void io(com.db4o.io.IoAdapter adapter)
		{
			globalSettingOnly();
			i_ioAdapter = adapter;
		}

		public void lockDatabaseFile(bool flag)
		{
			i_lockFile = flag;
		}

		public void markTransient(string marker)
		{
			com.db4o.Platform.markTransient(marker);
		}

		public void messageLevel(int level)
		{
			i_messageLevel = level;
			if (i_outStream == null)
			{
				setOut(j4o.lang.JavaSystem._out);
			}
		}

		public com.db4o.config.ObjectClass objectClass(object clazz)
		{
			com.db4o.reflect.ReflectClass claxx = reflectorFor(clazz);
			if (claxx == null)
			{
				return null;
			}
			string className = claxx.getName();
			com.db4o.Config4Class c4c = (com.db4o.Config4Class)i_exceptionalClasses.get(className
				);
			if (c4c == null)
			{
				c4c = new com.db4o.Config4Class(this, className);
				i_exceptionalClasses.put(className, c4c);
			}
			return c4c;
		}

		internal j4o.io.PrintStream outStream()
		{
			return i_outStream == null ? j4o.lang.JavaSystem._out : i_outStream;
		}

		public void password(string pw)
		{
			globalSettingOnly();
			i_password = pw;
		}

		public void readOnly(bool flag)
		{
			globalSettingOnly();
			i_readonly = flag;
		}

		internal com.db4o.reflect.generic.GenericReflector reflector()
		{
			if (_reflector == null)
			{
				if (_configuredReflector == null)
				{
					_configuredReflector = com.db4o.Platform.createReflector(this);
				}
				_reflector = new com.db4o.reflect.generic.GenericReflector(null, _configuredReflector
					);
				_configuredReflector.setParent(_reflector);
			}
			if (!_reflector.hasTransaction() && i_stream != null)
			{
				_reflector.setTransaction(i_stream.i_systemTrans);
			}
			return _reflector;
		}

		public void reflectWith(com.db4o.reflect.Reflector reflect)
		{
			if (i_stream != null)
			{
				com.db4o.Db4o.throwRuntimeException(46);
			}
			if (reflect == null)
			{
				throw new System.ArgumentNullException();
			}
			_configuredReflector = reflect;
		}

		public void refreshClasses()
		{
			if (i_stream == null)
			{
				com.db4o.Db4o.forEachSession(new _AnonymousInnerClass311(this));
			}
			else
			{
				i_stream.refreshClasses();
			}
		}

		private sealed class _AnonymousInnerClass311 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass311(Config4Impl _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object obj)
			{
				com.db4o.YapStream ys = ((com.db4o.Session)obj).i_stream;
				if (!ys.isClosed())
				{
					ys.refreshClasses();
				}
			}

			private readonly Config4Impl _enclosing;
		}

		internal void rename(com.db4o.Rename a_rename)
		{
			if (i_rename == null)
			{
				i_rename = new com.db4o.Collection4();
			}
			i_rename.add(a_rename);
		}

		public void reserveStorageSpace(long byteCount)
		{
			i_reservedStorageSpace = (int)byteCount;
			if (i_reservedStorageSpace < 0)
			{
				i_reservedStorageSpace = 0;
			}
			if (i_stream != null)
			{
				i_stream.reserve(i_reservedStorageSpace);
			}
		}

		/// <summary>The ConfigImpl also is our messageSender</summary>
		public void send(object obj)
		{
			if (i_stream == null)
			{
				com.db4o.Db4o.forEachSession(new _AnonymousInnerClass347(this));
			}
			else
			{
				i_stream.send(obj);
			}
		}

		private sealed class _AnonymousInnerClass347 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass347(Config4Impl _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object session)
			{
				com.db4o.YapStream ys = ((com.db4o.Session)session).i_stream;
				if (!ys.isClosed())
				{
					ys.send(session);
				}
			}

			private readonly Config4Impl _enclosing;
		}

		public void setBlobPath(string path)
		{
			ensureDirExists(path);
			i_blobPath = path;
		}

		public void setClassLoader(j4o.lang.ClassLoader classLoader)
		{
			i_classLoader = classLoader;
		}

		public void setMessageRecipient(com.db4o.messaging.MessageRecipient messageRecipient
			)
		{
			i_messageRecipient = messageRecipient;
		}

		public void setOut(j4o.io.PrintStream outStream)
		{
			i_outStream = outStream;
			if (i_stream != null)
			{
				i_stream.logMsg(19, com.db4o.Db4o.version());
			}
			else
			{
				com.db4o.Db4o.logMsg(com.db4o.Db4o.i_config, 19, com.db4o.Db4o.version());
			}
		}

		public void singleThreadedClient(bool flag)
		{
			i_singleThreadedClient = flag;
		}

		public void testConstructors(bool flag)
		{
			i_testConstructors = flag;
		}

		public void timeoutClientSocket(int milliseconds)
		{
			i_timeoutClientSocket = milliseconds;
		}

		public void timeoutPingClients(int milliseconds)
		{
			i_timeoutPingClients = milliseconds;
		}

		public void timeoutServerSocket(int milliseconds)
		{
			i_timeoutServerSocket = milliseconds;
		}

		public void unicode(bool unicodeOn)
		{
			if (unicodeOn)
			{
				i_encoding = com.db4o.YapConst.UNICODE;
			}
			else
			{
				i_encoding = com.db4o.YapConst.ISO8859;
			}
		}

		public void updateDepth(int depth)
		{
			i_updateDepth = depth;
		}

		public void weakReferenceCollectionInterval(int milliseconds)
		{
			i_weakReferenceCollectionInterval = milliseconds;
		}

		public void weakReferences(bool flag)
		{
			i_weakReferences = flag;
		}

		internal com.db4o.reflect.ReflectClass reflectorFor(object clazz)
		{
			clazz = com.db4o.Platform.getClassForType(clazz);
			if (clazz is com.db4o.reflect.ReflectClass)
			{
				return (com.db4o.reflect.ReflectClass)clazz;
			}
			if (clazz is j4o.lang.Class)
			{
				return reflector().forClass((j4o.lang.Class)clazz);
			}
			if (clazz is string)
			{
				return reflector().forName((string)clazz);
			}
			return reflector().forObject(clazz);
		}
	}
}
