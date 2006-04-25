namespace com.db4o
{
	/// <summary>Configuration template for creating new db4o files</summary>
	/// <exclude></exclude>
	public sealed class Config4Impl : com.db4o.config.Configuration, com.db4o.foundation.DeepClone
		, com.db4o.messaging.MessageSender, com.db4o.config.FreespaceConfiguration
	{
		private com.db4o.foundation.KeySpecHashtable4 _config = new com.db4o.foundation.KeySpecHashtable4
			(50);

		private static readonly com.db4o.foundation.KeySpec ACTIVATION_DEPTH = new com.db4o.foundation.KeySpec
			(5);

		private static readonly com.db4o.foundation.KeySpec ALLOW_VERSION_UPDATES = new com.db4o.foundation.KeySpec
			(false);

		private static readonly com.db4o.foundation.KeySpec AUTOMATIC_SHUTDOWN = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec BLOCKSIZE = new com.db4o.foundation.KeySpec
			((byte)1);

		private static readonly com.db4o.foundation.KeySpec BLOBPATH = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec CALLBACKS = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec CALL_CONSTRUCTORS = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.DEFAULT);

		private static readonly com.db4o.foundation.KeySpec CLASS_ACTIVATION_DEPTH_CONFIGURABLE
			 = new com.db4o.foundation.KeySpec(true);

		private static readonly com.db4o.foundation.KeySpec CLASSLOADER = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec DETECT_SCHEMA_CHANGES = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec DISABLE_COMMIT_RECOVERY = new 
			com.db4o.foundation.KeySpec(false);

		private static readonly com.db4o.foundation.KeySpec DISCARD_FREESPACE = new com.db4o.foundation.KeySpec
			(0);

		private static readonly com.db4o.foundation.KeySpec ENCODING = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.UNICODE);

		private static readonly com.db4o.foundation.KeySpec ENCRYPT = new com.db4o.foundation.KeySpec
			(false);

		private static readonly com.db4o.foundation.KeySpec EXCEPTIONAL_CLASSES = new com.db4o.foundation.KeySpec
			(new com.db4o.foundation.Hashtable4(16));

		private static readonly com.db4o.foundation.KeySpec EXCEPTIONS_ON_NOT_STORABLE = 
			new com.db4o.foundation.KeySpec(false);

		private static readonly com.db4o.foundation.KeySpec FLUSH_FILE_BUFFERS = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec FREESPACE_SYSTEM = new com.db4o.foundation.KeySpec
			(com.db4o.inside.freespace.FreespaceManager.FM_DEFAULT);

		private static readonly com.db4o.foundation.KeySpec GENERATE_UUIDS = new com.db4o.foundation.KeySpec
			(0);

		private static readonly com.db4o.foundation.KeySpec GENERATE_VERSION_NUMBERS = new 
			com.db4o.foundation.KeySpec(0);

		private static readonly com.db4o.foundation.KeySpec INTERN_STRINGS = new com.db4o.foundation.KeySpec
			(false);

		private static readonly com.db4o.foundation.KeySpec IS_SERVER = new com.db4o.foundation.KeySpec
			(false);

		private static readonly com.db4o.foundation.KeySpec LOCK_FILE = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec MESSAGE_LEVEL = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.NONE);

		private static readonly com.db4o.foundation.KeySpec MESSAGE_RECIPIENT = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec OPTIMIZE_NQ = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec OUTSTREAM = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec PASSWORD = new com.db4o.foundation.KeySpec
			((string)null);

		private static readonly com.db4o.foundation.KeySpec READ_AS = new com.db4o.foundation.KeySpec
			(new com.db4o.foundation.Hashtable4(16));

		private static readonly com.db4o.foundation.KeySpec READ_ONLY = new com.db4o.foundation.KeySpec
			(false);

		private static readonly com.db4o.foundation.KeySpec CONFIGURED_REFLECTOR = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec REFLECTOR = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec RENAME = new com.db4o.foundation.KeySpec
			(null);

		private static readonly com.db4o.foundation.KeySpec RESERVED_STORAGE_SPACE = new 
			com.db4o.foundation.KeySpec(0);

		private static readonly com.db4o.foundation.KeySpec SINGLE_THREADED_CLIENT = new 
			com.db4o.foundation.KeySpec(false);

		private static readonly com.db4o.foundation.KeySpec TEST_CONSTRUCTORS = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec TIMEOUT_CLIENT_SOCKET = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.CLIENT_SOCKET_TIMEOUT);

		private static readonly com.db4o.foundation.KeySpec TIMEOUT_PING_CLIENTS = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.CONNECTION_TIMEOUT);

		private static readonly com.db4o.foundation.KeySpec TIMEOUT_SERVER_SOCKET = new com.db4o.foundation.KeySpec
			(com.db4o.YapConst.SERVER_SOCKET_TIMEOUT);

		private static readonly com.db4o.foundation.KeySpec UPDATE_DEPTH = new com.db4o.foundation.KeySpec
			(0);

		private static readonly com.db4o.foundation.KeySpec WEAK_REFERENCE_COLLECTION_INTERVAL
			 = new com.db4o.foundation.KeySpec(1000);

		private static readonly com.db4o.foundation.KeySpec WEAK_REFERENCES = new com.db4o.foundation.KeySpec
			(true);

		private static readonly com.db4o.foundation.KeySpec IOADAPTER = new com.db4o.foundation.KeySpec
			(new com.db4o.io.RandomAccessFileAdapter());

		private static readonly com.db4o.foundation.KeySpec ALIASES = new com.db4o.foundation.KeySpec
			(null);

		private com.db4o.YapStream i_stream;

		internal int activationDepth()
		{
			return _config.getAsInt(ACTIVATION_DEPTH);
		}

		public void activationDepth(int depth)
		{
			_config.put(ACTIVATION_DEPTH, depth);
		}

		public void allowVersionUpdates(bool flag)
		{
			_config.put(ALLOW_VERSION_UPDATES, flag);
		}

		public void automaticShutDown(bool flag)
		{
			_config.put(AUTOMATIC_SHUTDOWN, flag);
		}

		public void blockSize(int bytes)
		{
			if (bytes < 1 || bytes > 127)
			{
				com.db4o.inside.Exceptions4.throwRuntimeException(1);
			}
			if (i_stream != null)
			{
				com.db4o.inside.Exceptions4.throwRuntimeException(46);
			}
			_config.put(BLOCKSIZE, (byte)bytes);
		}

		public void callbacks(bool turnOn)
		{
			_config.put(CALLBACKS, turnOn);
		}

		public void callConstructors(bool flag)
		{
			_config.put(CALL_CONSTRUCTORS, (flag ? com.db4o.YapConst.YES : com.db4o.YapConst.
				NO));
		}

		public void classActivationDepthConfigurable(bool turnOn)
		{
			_config.put(CLASS_ACTIVATION_DEPTH_CONFIGURABLE, turnOn);
		}

		internal com.db4o.Config4Class configClass(string className)
		{
			com.db4o.Config4Class config = (com.db4o.Config4Class)exceptionalClasses().get(className
				);
			return config;
		}

		public object deepClone(object param)
		{
			com.db4o.Config4Impl ret = new com.db4o.Config4Impl();
			ret._config = (com.db4o.foundation.KeySpecHashtable4)_config.deepClone(this);
			ret.i_stream = (com.db4o.YapStream)param;
			return ret;
		}

		public void detectSchemaChanges(bool flag)
		{
			_config.put(DETECT_SCHEMA_CHANGES, flag);
		}

		public void disableCommitRecovery()
		{
			_config.put(DISABLE_COMMIT_RECOVERY, true);
		}

		public void discardFreeSpace(int bytes)
		{
			_config.put(DISCARD_FREESPACE, bytes);
		}

		public void discardSmallerThan(int byteCount)
		{
			discardFreeSpace(byteCount);
		}

		public void encrypt(bool flag)
		{
			globalSettingOnly();
			_config.put(ENCRYPT, flag);
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
				throw new System.IO.IOException(com.db4o.Messages.get(37, path));
			}
		}

		internal j4o.io.PrintStream errStream()
		{
			j4o.io.PrintStream outStream = outStreamOrNull();
			return outStream == null ? j4o.lang.JavaSystem.err : outStream;
		}

		public void exceptionsOnNotStorable(bool flag)
		{
			_config.put(EXCEPTIONS_ON_NOT_STORABLE, flag);
		}

		public void flushFileBuffers(bool flag)
		{
			_config.put(FLUSH_FILE_BUFFERS, flag);
		}

		public com.db4o.config.FreespaceConfiguration freespace()
		{
			return this;
		}

		public void generateUUIDs(int setting)
		{
			_config.put(GENERATE_UUIDS, setting);
			storeStreamBootRecord();
		}

		private void storeStreamBootRecord()
		{
			if (i_stream == null)
			{
				return;
			}
			com.db4o.PBootRecord bootRecord = i_stream.bootRecord();
			if (bootRecord != null)
			{
				bootRecord.initConfig(this);
				com.db4o.Transaction trans = i_stream.getSystemTransaction();
				i_stream.setInternal(trans, bootRecord, false);
				trans.commit();
			}
		}

		public void generateVersionNumbers(int setting)
		{
			_config.put(GENERATE_VERSION_NUMBERS, setting);
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
				com.db4o.inside.Exceptions4.throwRuntimeException(46);
			}
		}

		public void internStrings(bool doIntern)
		{
			_config.put(INTERN_STRINGS, doIntern);
		}

		public void io(com.db4o.io.IoAdapter adapter)
		{
			globalSettingOnly();
			_config.put(IOADAPTER, adapter);
		}

		public void lockDatabaseFile(bool flag)
		{
			_config.put(LOCK_FILE, flag);
		}

		public void markTransient(string marker)
		{
			com.db4o.Platform4.markTransient(marker);
		}

		public void messageLevel(int level)
		{
			_config.put(MESSAGE_LEVEL, level);
			if (outStream() == null)
			{
				setOut(j4o.lang.JavaSystem._out);
			}
		}

		public void optimizeNativeQueries(bool optimizeNQ)
		{
			_config.put(OPTIMIZE_NQ, optimizeNQ);
		}

		public bool optimizeNativeQueries()
		{
			return _config.getAsBoolean(OPTIMIZE_NQ);
		}

		public com.db4o.config.ObjectClass objectClass(object clazz)
		{
			string className = null;
			if (clazz is string)
			{
				className = (string)clazz;
			}
			else
			{
				com.db4o.reflect.ReflectClass claxx = reflectorFor(clazz);
				if (claxx == null)
				{
					return null;
				}
				className = claxx.getName();
			}
			com.db4o.foundation.Hashtable4 xClasses = exceptionalClasses();
			com.db4o.Config4Class c4c = (com.db4o.Config4Class)xClasses.get(className);
			if (c4c == null)
			{
				c4c = new com.db4o.Config4Class(this, className);
				xClasses.put(className, c4c);
			}
			return c4c;
		}

		private j4o.io.PrintStream outStreamOrNull()
		{
			return (j4o.io.PrintStream)_config.get(OUTSTREAM);
		}

		internal j4o.io.PrintStream outStream()
		{
			j4o.io.PrintStream outStream = outStreamOrNull();
			return outStream == null ? j4o.lang.JavaSystem._out : outStream;
		}

		public void password(string pw)
		{
			globalSettingOnly();
			_config.put(PASSWORD, pw);
		}

		public void readOnly(bool flag)
		{
			globalSettingOnly();
			_config.put(READ_ONLY, flag);
		}

		internal com.db4o.reflect.generic.GenericReflector reflector()
		{
			com.db4o.reflect.generic.GenericReflector reflector = (com.db4o.reflect.generic.GenericReflector
				)_config.get(REFLECTOR);
			if (reflector == null)
			{
				com.db4o.reflect.Reflector configuredReflector = (com.db4o.reflect.Reflector)_config
					.get(CONFIGURED_REFLECTOR);
				if (configuredReflector == null)
				{
					configuredReflector = com.db4o.Platform4.createReflector(classLoader());
					_config.put(CONFIGURED_REFLECTOR, configuredReflector);
				}
				reflector = new com.db4o.reflect.generic.GenericReflector(null, configuredReflector
					);
				_config.put(REFLECTOR, reflector);
				configuredReflector.setParent(reflector);
			}
			if (!reflector.hasTransaction() && i_stream != null)
			{
				reflector.setTransaction(i_stream.i_systemTrans);
			}
			return reflector;
		}

		public void reflectWith(com.db4o.reflect.Reflector reflect)
		{
			if (i_stream != null)
			{
				com.db4o.inside.Exceptions4.throwRuntimeException(46);
			}
			if (reflect == null)
			{
				throw new System.ArgumentNullException();
			}
			_config.put(CONFIGURED_REFLECTOR, reflect);
			_config.put(REFLECTOR, null);
		}

		public void refreshClasses()
		{
			if (i_stream == null)
			{
				com.db4o.Db4o.forEachSession(new _AnonymousInnerClass400(this));
			}
			else
			{
				i_stream.refreshClasses();
			}
		}

		private sealed class _AnonymousInnerClass400 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass400(Config4Impl _enclosing)
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
			com.db4o.foundation.Collection4 renameCollection = rename();
			if (renameCollection == null)
			{
				renameCollection = new com.db4o.foundation.Collection4();
				_config.put(RENAME, renameCollection);
			}
			renameCollection.add(a_rename);
		}

		public void reserveStorageSpace(long byteCount)
		{
			int reservedStorageSpace = (int)byteCount;
			if (reservedStorageSpace < 0)
			{
				reservedStorageSpace = 0;
			}
			_config.put(RESERVED_STORAGE_SPACE, reservedStorageSpace);
			if (i_stream != null)
			{
				i_stream.reserve(reservedStorageSpace);
			}
		}

		/// <summary>The ConfigImpl also is our messageSender</summary>
		public void send(object obj)
		{
			if (i_stream == null)
			{
				com.db4o.Db4o.forEachSession(new _AnonymousInnerClass439(this));
			}
			else
			{
				i_stream.send(obj);
			}
		}

		private sealed class _AnonymousInnerClass439 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass439(Config4Impl _enclosing)
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
			_config.put(BLOBPATH, path);
		}

		public void setClassLoader(object classLoader)
		{
			reflectWith(com.db4o.Platform4.createReflector(classLoader));
		}

		public void setMessageRecipient(com.db4o.messaging.MessageRecipient messageRecipient
			)
		{
			_config.put(MESSAGE_RECIPIENT, messageRecipient);
		}

		public void setOut(j4o.io.PrintStream outStream)
		{
			_config.put(OUTSTREAM, outStream);
			if (i_stream != null)
			{
				i_stream.logMsg(19, com.db4o.Db4o.version());
			}
			else
			{
				com.db4o.Messages.logMsg(com.db4o.Db4o.i_config, 19, com.db4o.Db4o.version());
			}
		}

		public void singleThreadedClient(bool flag)
		{
			_config.put(SINGLE_THREADED_CLIENT, flag);
		}

		public void testConstructors(bool flag)
		{
			_config.put(TEST_CONSTRUCTORS, flag);
		}

		public void timeoutClientSocket(int milliseconds)
		{
			_config.put(TIMEOUT_CLIENT_SOCKET, milliseconds);
		}

		public void timeoutPingClients(int milliseconds)
		{
			_config.put(TIMEOUT_PING_CLIENTS, milliseconds);
		}

		public void timeoutServerSocket(int milliseconds)
		{
			_config.put(TIMEOUT_SERVER_SOCKET, milliseconds);
		}

		public void unicode(bool unicodeOn)
		{
			_config.put(ENCODING, (unicodeOn ? com.db4o.YapConst.UNICODE : com.db4o.YapConst.
				ISO8859));
		}

		public void updateDepth(int depth)
		{
			_config.put(UPDATE_DEPTH, depth);
		}

		public void useRamSystem()
		{
			_config.put(FREESPACE_SYSTEM, com.db4o.inside.freespace.FreespaceManager.FM_RAM);
		}

		public void useIndexSystem()
		{
			_config.put(FREESPACE_SYSTEM, com.db4o.inside.freespace.FreespaceManager.FM_IX);
		}

		public void weakReferenceCollectionInterval(int milliseconds)
		{
			_config.put(WEAK_REFERENCE_COLLECTION_INTERVAL, milliseconds);
		}

		public void weakReferences(bool flag)
		{
			_config.put(WEAK_REFERENCES, flag);
		}

		private com.db4o.foundation.Collection4 aliases()
		{
			com.db4o.foundation.Collection4 aliasesCollection = (com.db4o.foundation.Collection4
				)_config.get(ALIASES);
			if (null == aliasesCollection)
			{
				aliasesCollection = new com.db4o.foundation.Collection4();
				_config.put(ALIASES, aliasesCollection);
			}
			return aliasesCollection;
		}

		public void addAlias(com.db4o.config.Alias alias)
		{
			if (null == alias)
			{
				throw new System.ArgumentException("alias");
			}
			aliases().add(alias);
		}

		public string resolveAlias(string runtimeType)
		{
			com.db4o.foundation.Collection4 configuredAliases = aliases();
			if (null == configuredAliases)
			{
				return runtimeType;
			}
			com.db4o.foundation.Iterator4 i = configuredAliases.iterator();
			while (i.hasNext())
			{
				string resolved = ((com.db4o.config.Alias)i.next()).resolve(runtimeType);
				if (null != resolved)
				{
					return resolved;
				}
			}
			return runtimeType;
		}

		internal com.db4o.reflect.ReflectClass reflectorFor(object clazz)
		{
			clazz = com.db4o.Platform4.getClassForType(clazz);
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

		public bool allowVersionUpdates()
		{
			return _config.getAsBoolean(ALLOW_VERSION_UPDATES);
		}

		internal bool automaticShutDown()
		{
			return _config.getAsBoolean(AUTOMATIC_SHUTDOWN);
		}

		internal byte blockSize()
		{
			return _config.getAsByte(BLOCKSIZE);
		}

		internal string blobPath()
		{
			return _config.getAsString(BLOBPATH);
		}

		internal bool callbacks()
		{
			return _config.getAsBoolean(CALLBACKS);
		}

		internal int callConstructors()
		{
			return _config.getAsInt(CALL_CONSTRUCTORS);
		}

		internal bool classActivationDepthConfigurable()
		{
			return _config.getAsBoolean(CLASS_ACTIVATION_DEPTH_CONFIGURABLE);
		}

		internal object classLoader()
		{
			return _config.get(CLASSLOADER);
		}

		internal bool detectSchemaChanges()
		{
			return _config.getAsBoolean(DETECT_SCHEMA_CHANGES);
		}

		internal bool commitRecoveryDisabled()
		{
			return _config.getAsBoolean(DISABLE_COMMIT_RECOVERY);
		}

		public int discardFreeSpace()
		{
			return _config.getAsInt(DISCARD_FREESPACE);
		}

		internal byte encoding()
		{
			return _config.getAsByte(ENCODING);
		}

		internal bool encrypt()
		{
			return _config.getAsBoolean(ENCRYPT);
		}

		internal com.db4o.foundation.Hashtable4 exceptionalClasses()
		{
			return (com.db4o.foundation.Hashtable4)_config.get(EXCEPTIONAL_CLASSES);
		}

		internal bool exceptionsOnNotStorable()
		{
			return _config.getAsBoolean(EXCEPTIONS_ON_NOT_STORABLE);
		}

		public bool flushFileBuffers()
		{
			return _config.getAsBoolean(FLUSH_FILE_BUFFERS);
		}

		internal byte freespaceSystem()
		{
			return _config.getAsByte(FREESPACE_SYSTEM);
		}

		internal int generateUUIDs()
		{
			return _config.getAsInt(GENERATE_UUIDS);
		}

		internal int generateVersionNumbers()
		{
			return _config.getAsInt(GENERATE_VERSION_NUMBERS);
		}

		internal bool internStrings()
		{
			return _config.getAsBoolean(INTERN_STRINGS);
		}

		internal void isServer(bool flag)
		{
			_config.put(IS_SERVER, flag);
		}

		internal bool isServer()
		{
			return _config.getAsBoolean(IS_SERVER);
		}

		internal bool lockFile()
		{
			return _config.getAsBoolean(LOCK_FILE);
		}

		internal int messageLevel()
		{
			return _config.getAsInt(MESSAGE_LEVEL);
		}

		internal com.db4o.messaging.MessageRecipient messageRecipient()
		{
			return (com.db4o.messaging.MessageRecipient)_config.get(MESSAGE_RECIPIENT);
		}

		internal bool optimizeNQ()
		{
			return _config.getAsBoolean(OPTIMIZE_NQ);
		}

		internal string password()
		{
			return _config.getAsString(PASSWORD);
		}

		internal com.db4o.foundation.Hashtable4 readAs()
		{
			return (com.db4o.foundation.Hashtable4)_config.get(READ_AS);
		}

		internal bool isReadOnly()
		{
			return _config.getAsBoolean(READ_ONLY);
		}

		internal com.db4o.foundation.Collection4 rename()
		{
			return (com.db4o.foundation.Collection4)_config.get(RENAME);
		}

		internal int reservedStorageSpace()
		{
			return _config.getAsInt(RESERVED_STORAGE_SPACE);
		}

		internal bool singleThreadedClient()
		{
			return _config.getAsBoolean(SINGLE_THREADED_CLIENT);
		}

		internal bool testConstructors()
		{
			return _config.getAsBoolean(TEST_CONSTRUCTORS);
		}

		internal int timeoutClientSocket()
		{
			return _config.getAsInt(TIMEOUT_CLIENT_SOCKET);
		}

		internal int timeoutPingClients()
		{
			return _config.getAsInt(TIMEOUT_PING_CLIENTS);
		}

		internal int timeoutServerSocket()
		{
			return _config.getAsInt(TIMEOUT_SERVER_SOCKET);
		}

		internal int updateDepth()
		{
			return _config.getAsInt(UPDATE_DEPTH);
		}

		internal int weakReferenceCollectionInterval()
		{
			return _config.getAsInt(WEAK_REFERENCE_COLLECTION_INTERVAL);
		}

		internal bool weakReferences()
		{
			return _config.getAsBoolean(WEAK_REFERENCES);
		}

		internal com.db4o.io.IoAdapter ioAdapter()
		{
			return (com.db4o.io.IoAdapter)_config.get(IOADAPTER);
		}
	}
}
