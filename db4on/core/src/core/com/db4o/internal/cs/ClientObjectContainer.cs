namespace com.db4o.@internal.cs
{
	/// <exclude></exclude>
	public class ClientObjectContainer : com.db4o.@internal.ObjectContainerBase, com.db4o.ext.ExtClient
		, com.db4o.BlobTransport
	{
		internal readonly object blobLock = new object();

		private com.db4o.@internal.cs.BlobProcessor blobThread;

		private com.db4o.foundation.network.Socket4 i_socket;

		internal com.db4o.foundation.Queue4 messageQueue = new com.db4o.foundation.Queue4
			();

		internal readonly com.db4o.foundation.Lock4 messageQueueLock = new com.db4o.foundation.Lock4
			();

		private string password;

		internal int[] _prefetchedIDs;

		private com.db4o.@internal.cs.ClientMessageDispatcher _readerThread;

		internal int remainingIDs;

		private string switchedToFile;

		private bool _singleThreaded;

		private string userName;

		private com.db4o.ext.Db4oDatabase i_db;

		protected bool _doFinalize = true;

		private int _blockSize = 1;

		private com.db4o.foundation.Collection4 _batchedMessages = new com.db4o.foundation.Collection4
			();

		private int _batchedQueueLength = com.db4o.@internal.Const4.INT_LENGTH;

		private ClientObjectContainer(com.db4o.config.Configuration config) : base(config
			, null)
		{
		}

		public ClientObjectContainer(string fakeServerFile) : this(com.db4o.Db4o.CloneConfiguration
			())
		{
			lock (Lock())
			{
				_singleThreaded = ConfigImpl().SingleThreadedClient();
				throw new System.Exception("This constructor is for Debug.fakeServer use only.");
				Initialize3();
				com.db4o.@internal.Platform4.PostOpen(this);
			}
		}

		public ClientObjectContainer(com.db4o.config.Configuration config, com.db4o.foundation.network.Socket4
			 socket, string user, string password_, bool login) : this(config)
		{
			lock (Lock())
			{
				_singleThreaded = ConfigImpl().SingleThreadedClient();
				if (password_ == null)
				{
					throw new System.ArgumentNullException(com.db4o.@internal.Messages.Get(56));
				}
				if (!login)
				{
					password_ = null;
				}
				userName = user;
				password = password_;
				i_socket = socket;
				try
				{
					LoginToServer(socket);
				}
				catch (System.IO.IOException e)
				{
					StopSession();
					throw;
				}
				if (!_singleThreaded)
				{
					StartReaderThread(socket, user);
				}
				LogMsg(36, ToString());
				ReadThis();
				Initialize3();
				com.db4o.@internal.Platform4.PostOpen(this);
			}
		}

		private void StartReaderThread(com.db4o.foundation.network.Socket4 socket, string
			 user)
		{
			_readerThread = new com.db4o.@internal.cs.ClientMessageDispatcher(this, socket, messageQueue
				, messageQueueLock);
			_readerThread.SetName("db4o message client for user " + user);
			_readerThread.Start();
		}

		public override void Backup(string path)
		{
			com.db4o.@internal.Exceptions4.ThrowRuntimeException(60);
		}

		public virtual void BlockSize(int blockSize)
		{
			_blockSize = blockSize;
		}

		public override byte BlockSize()
		{
			return (byte)_blockSize;
		}

		protected override bool Close2()
		{
			if (_readerThread == null || _readerThread.IsClosed())
			{
				return base.Close2();
			}
			try
			{
				WriteMsg(com.db4o.@internal.cs.messages.Msg.COMMIT_OK, true);
				ExpectedResponse(com.db4o.@internal.cs.messages.Msg.OK);
			}
			catch (System.Exception e)
			{
				com.db4o.@internal.Exceptions4.CatchAllExceptDb4oException(e);
			}
			try
			{
				WriteMsg(com.db4o.@internal.cs.messages.Msg.CLOSE, true);
			}
			catch (System.Exception e)
			{
				com.db4o.@internal.Exceptions4.CatchAllExceptDb4oException(e);
			}
			try
			{
				if (!_singleThreaded)
				{
					_readerThread.Close();
				}
			}
			catch (System.Exception e)
			{
				com.db4o.@internal.Exceptions4.CatchAllExceptDb4oException(e);
			}
			try
			{
				i_socket.Close();
			}
			catch (System.Exception e)
			{
				com.db4o.@internal.Exceptions4.CatchAllExceptDb4oException(e);
			}
			bool ret = base.Close2();
			return ret;
		}

		public sealed override void Commit1()
		{
			i_trans.Commit();
		}

		public override int ConverterVersion()
		{
			return com.db4o.@internal.convert.Converter.VERSION;
		}

		internal virtual com.db4o.foundation.network.Socket4 CreateParalellSocket()
		{
			WriteMsg(com.db4o.@internal.cs.messages.Msg.GET_THREAD_ID, true);
			int serverThreadID = ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg.ID_LIST
				).ReadInt();
			com.db4o.foundation.network.Socket4 sock = i_socket.OpenParalellSocket();
			if (!(i_socket is com.db4o.foundation.network.LoopbackSocket))
			{
				LoginToServer(sock);
			}
			if (switchedToFile != null)
			{
				com.db4o.@internal.cs.messages.MsgD message = com.db4o.@internal.cs.messages.Msg.
					SWITCH_TO_FILE.GetWriterForString(i_systemTrans, switchedToFile);
				message.Write(this, sock);
				if (!(com.db4o.@internal.cs.messages.Msg.OK.Equals(com.db4o.@internal.cs.messages.Msg
					.ReadMessage(i_systemTrans, sock))))
				{
					throw new System.IO.IOException(com.db4o.@internal.Messages.Get(42));
				}
			}
			com.db4o.@internal.cs.messages.Msg.USE_TRANSACTION.GetWriterForInt(i_trans, serverThreadID
				).Write(this, sock);
			return sock;
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult NewQueryResult
			(com.db4o.@internal.Transaction trans, com.db4o.config.QueryEvaluationMode mode)
		{
			throw new System.InvalidOperationException();
		}

		public sealed override com.db4o.@internal.Transaction NewTransaction(com.db4o.@internal.Transaction
			 parentTransaction)
		{
			return new com.db4o.@internal.cs.ClientTransaction(this, parentTransaction);
		}

		public override bool CreateYapClass(com.db4o.@internal.ClassMetadata a_yapClass, 
			com.db4o.reflect.ReflectClass a_class, com.db4o.@internal.ClassMetadata a_superYapClass
			)
		{
			WriteMsg(com.db4o.@internal.cs.messages.Msg.CREATE_CLASS.GetWriterForString(i_systemTrans
				, a_class.GetName()), true);
			com.db4o.@internal.cs.messages.Msg resp = GetResponse();
			if (resp == null)
			{
				return false;
			}
			if (resp.Equals(com.db4o.@internal.cs.messages.Msg.FAILED))
			{
				SendClassMeta(a_class);
				resp = GetResponse();
			}
			if (resp.Equals(com.db4o.@internal.cs.messages.Msg.FAILED))
			{
				if (ConfigImpl().ExceptionsOnNotStorable())
				{
					throw new com.db4o.ext.ObjectNotStorableException(a_class);
				}
				return false;
			}
			if (!resp.Equals(com.db4o.@internal.cs.messages.Msg.OBJECT_TO_CLIENT))
			{
				return false;
			}
			com.db4o.@internal.cs.messages.MsgObject message = (com.db4o.@internal.cs.messages.MsgObject
				)resp;
			com.db4o.@internal.StatefulBuffer bytes = message.Unmarshall();
			if (bytes == null)
			{
				return false;
			}
			bytes.SetTransaction(GetSystemTransaction());
			if (!base.CreateYapClass(a_yapClass, a_class, a_superYapClass))
			{
				return false;
			}
			a_yapClass.SetID(message.GetId());
			a_yapClass.ReadName1(GetSystemTransaction(), bytes);
			ClassCollection().AddYapClass(a_yapClass);
			ClassCollection().ReadYapClass(a_yapClass, a_class);
			return true;
		}

		private void SendClassMeta(com.db4o.reflect.ReflectClass reflectClass)
		{
			com.db4o.@internal.cs.ClassInfo classMeta = _classMetaHelper.GetClassMeta(reflectClass
				);
			WriteMsg(com.db4o.@internal.cs.messages.Msg.CLASS_META.GetWriter(Marshall(i_systemTrans
				, classMeta)), true);
		}

		public override long CurrentVersion()
		{
			WriteMsg(com.db4o.@internal.cs.messages.Msg.CURRENT_VERSION, true);
			return ((com.db4o.@internal.cs.messages.MsgD)ExpectedResponse(com.db4o.@internal.cs.messages.Msg
				.ID_LIST)).ReadLong();
		}

		public sealed override bool Delete4(com.db4o.@internal.Transaction ta, com.db4o.@internal.ObjectReference
			 yo, int a_cascade, bool userCall)
		{
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.DELETE
				.GetWriterForInts(i_trans, new int[] { yo.GetID(), userCall ? 1 : 0 });
			WriteMsg(msg, false);
			return true;
		}

		public override bool DetectSchemaChanges()
		{
			return false;
		}

		protected override bool DoFinalize()
		{
			return _doFinalize;
		}

		internal com.db4o.@internal.Buffer ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg
			 expectedMessage)
		{
			com.db4o.@internal.cs.messages.Msg msg = ExpectedResponse(expectedMessage);
			if (msg == null)
			{
				return null;
			}
			return msg.GetByteLoad();
		}

		internal com.db4o.@internal.cs.messages.Msg ExpectedResponse(com.db4o.@internal.cs.messages.Msg
			 expectedMessage)
		{
			com.db4o.@internal.cs.messages.Msg message = GetResponse();
			if (expectedMessage.Equals(message))
			{
				return message;
			}
			return null;
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult GetAll(com.db4o.@internal.Transaction
			 trans)
		{
			int mode = Config().QueryEvaluationMode().AsInt();
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.GET_ALL
				.GetWriterForInt(trans, mode);
			WriteMsg(msg, true);
			return ReadQueryResult(trans);
		}

		/// <summary>may return null, if no message is returned.</summary>
		/// <remarks>
		/// may return null, if no message is returned. Error handling is weak and
		/// should ideally be able to trigger some sort of state listener (connection
		/// dead) on the client.
		/// </remarks>
		internal virtual com.db4o.@internal.cs.messages.Msg GetResponse()
		{
			return _singleThreaded ? GetResponseSingleThreaded() : GetResponseMultiThreaded();
		}

		private com.db4o.@internal.cs.messages.Msg GetResponseMultiThreaded()
		{
			try
			{
				return (com.db4o.@internal.cs.messages.Msg)messageQueueLock.Run(new _AnonymousInnerClass334
					(this));
			}
			catch (System.Exception ex)
			{
				com.db4o.@internal.Exceptions4.CatchAllExceptDb4oException(ex);
				return null;
			}
		}

		private sealed class _AnonymousInnerClass334 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass334(ClientObjectContainer _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object Run()
			{
				com.db4o.@internal.cs.messages.Msg message = this.RetrieveMessage();
				if (message != null)
				{
					return message;
				}
				this.ThrowOnClosed();
				this._enclosing.messageQueueLock.Snooze(this._enclosing.ConfigImpl().TimeoutClientSocket
					());
				this.ThrowOnClosed();
				return this.RetrieveMessage();
			}

			private void ThrowOnClosed()
			{
				if (this._enclosing._readerThread.IsClosed())
				{
					this._enclosing._doFinalize = false;
					throw new com.db4o.ext.Db4oException(com.db4o.@internal.Messages.Get(com.db4o.@internal.Messages
						.CLOSED_OR_OPEN_FAILED));
				}
			}

			private com.db4o.@internal.cs.messages.Msg RetrieveMessage()
			{
				com.db4o.@internal.cs.messages.Msg message = null;
				message = (com.db4o.@internal.cs.messages.Msg)this._enclosing.messageQueue.Next();
				if (message != null)
				{
					if (com.db4o.@internal.cs.messages.Msg.ERROR.Equals(message))
					{
						throw new com.db4o.ext.Db4oException("Client connection error");
					}
				}
				return message;
			}

			private readonly ClientObjectContainer _enclosing;
		}

		private com.db4o.@internal.cs.messages.Msg GetResponseSingleThreaded()
		{
			while (i_socket != null)
			{
				try
				{
					com.db4o.@internal.cs.messages.Msg message = com.db4o.@internal.cs.messages.Msg.ReadMessage
						(i_trans, i_socket);
					if (com.db4o.@internal.cs.messages.Msg.PING.Equals(message))
					{
						WriteMsg(com.db4o.@internal.cs.messages.Msg.OK, true);
					}
					else
					{
						if (com.db4o.@internal.cs.messages.Msg.CLOSE.Equals(message))
						{
							LogMsg(35, ToString());
							Close();
							return null;
						}
						else
						{
							if (message != null)
							{
								return message;
							}
						}
					}
				}
				catch
				{
				}
			}
			return null;
		}

		public override com.db4o.@internal.ClassMetadata GetYapClass(int a_id)
		{
			if (a_id == 0)
			{
				return null;
			}
			com.db4o.@internal.ClassMetadata yc = base.GetYapClass(a_id);
			if (yc != null)
			{
				return yc;
			}
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.CLASS_NAME_FOR_ID
				.GetWriterForInt(i_systemTrans, a_id);
			WriteMsg(msg, true);
			com.db4o.@internal.cs.messages.MsgD message = (com.db4o.@internal.cs.messages.MsgD
				)ExpectedResponse(com.db4o.@internal.cs.messages.Msg.CLASS_NAME_FOR_ID);
			string className = message.ReadString();
			if (className != null && className.Length > 0)
			{
				com.db4o.reflect.ReflectClass claxx = Reflector().ForName(className);
				if (claxx != null)
				{
					return ProduceYapClass(claxx);
				}
			}
			return null;
		}

		public override bool NeedsLockFileThread()
		{
			return false;
		}

		protected override bool HasShutDownHook()
		{
			return false;
		}

		public override com.db4o.ext.Db4oDatabase Identity()
		{
			if (i_db == null)
			{
				WriteMsg(com.db4o.@internal.cs.messages.Msg.IDENTITY, true);
				com.db4o.@internal.Buffer reader = ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg
					.ID_LIST);
				ShowInternalClasses(true);
				i_db = (com.db4o.ext.Db4oDatabase)GetByID(reader.ReadInt());
				Activate1(i_systemTrans, i_db, 3);
				ShowInternalClasses(false);
			}
			return i_db;
		}

		public override bool IsClient()
		{
			return true;
		}

		internal virtual void LoginToServer(com.db4o.foundation.network.Socket4 a_socket)
		{
			if (password != null)
			{
				com.db4o.@internal.UnicodeStringIO stringWriter = new com.db4o.@internal.UnicodeStringIO
					();
				int length = stringWriter.Length(userName) + stringWriter.Length(password);
				com.db4o.@internal.cs.messages.MsgD message = com.db4o.@internal.cs.messages.Msg.
					LOGIN.GetWriterForLength(i_systemTrans, length);
				message.WriteString(userName);
				message.WriteString(password);
				message.Write(this, a_socket);
				com.db4o.@internal.cs.messages.Msg msg = com.db4o.@internal.cs.messages.Msg.ReadMessage
					(i_systemTrans, a_socket);
				if (!com.db4o.@internal.cs.messages.Msg.LOGIN_OK.Equals(msg))
				{
					throw new System.IO.IOException(com.db4o.@internal.Messages.Get(42));
				}
				com.db4o.@internal.Buffer payLoad = msg.PayLoad();
				_blockSize = payLoad.ReadInt();
				int doEncrypt = payLoad.ReadInt();
				if (doEncrypt == 0)
				{
					i_handlers.OldEncryptionOff();
				}
			}
		}

		public override bool MaintainsIndices()
		{
			return false;
		}

		public sealed override int NewUserObject()
		{
			int prefetchIDCount = Config().PrefetchIDCount();
			EnsureIDCacheAllocated(prefetchIDCount);
			com.db4o.@internal.Buffer reader = null;
			if (remainingIDs < 1)
			{
				com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.PREFETCH_IDS
					.GetWriterForInt(i_trans, prefetchIDCount);
				WriteMsg(msg, true);
				reader = ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg.ID_LIST);
				for (int i = prefetchIDCount - 1; i >= 0; i--)
				{
					_prefetchedIDs[i] = reader.ReadInt();
				}
				remainingIDs = prefetchIDCount;
			}
			remainingIDs--;
			return _prefetchedIDs[remainingIDs];
		}

		public virtual int PrefetchObjects(com.db4o.foundation.IntIterator4 ids, object[]
			 prefetched, int prefetchCount)
		{
			int count = 0;
			int toGet = 0;
			int[] idsToGet = new int[prefetchCount];
			int[] position = new int[prefetchCount];
			while (count < prefetchCount)
			{
				if (!ids.MoveNext())
				{
					break;
				}
				int id = ids.CurrentInt();
				if (id > 0)
				{
					object obj = ObjectForIDFromCache(id);
					if (obj != null)
					{
						prefetched[count] = obj;
					}
					else
					{
						idsToGet[toGet] = id;
						position[toGet] = count;
						toGet++;
					}
					count++;
				}
			}
			if (toGet > 0)
			{
				com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.READ_MULTIPLE_OBJECTS
					.GetWriterForIntArray(i_trans, idsToGet, toGet);
				WriteMsg(msg, true);
				com.db4o.@internal.cs.messages.MsgD message = (com.db4o.@internal.cs.messages.MsgD
					)ExpectedResponse(com.db4o.@internal.cs.messages.Msg.READ_MULTIPLE_OBJECTS);
				int embeddedMessageCount = message.ReadInt();
				for (int i = 0; i < embeddedMessageCount; i++)
				{
					com.db4o.@internal.cs.messages.MsgObject mso = (com.db4o.@internal.cs.messages.MsgObject
						)com.db4o.@internal.cs.messages.Msg.OBJECT_TO_CLIENT.Clone(GetTransaction());
					mso.PayLoad(message.PayLoad().ReadYapBytes());
					if (mso.PayLoad() != null)
					{
						mso.PayLoad().IncrementOffset(com.db4o.@internal.Const4.MESSAGE_LENGTH);
						com.db4o.@internal.StatefulBuffer reader = mso.Unmarshall(com.db4o.@internal.Const4
							.MESSAGE_LENGTH);
						object obj = ObjectForIDFromCache(idsToGet[i]);
						if (obj != null)
						{
							prefetched[position[i]] = obj;
						}
						else
						{
							prefetched[position[i]] = new com.db4o.@internal.ObjectReference(idsToGet[i]).ReadPrefetch
								(this, reader);
						}
					}
				}
			}
			return count;
		}

		internal virtual void ProcessBlobMessage(com.db4o.@internal.cs.messages.MsgBlob msg
			)
		{
			lock (blobLock)
			{
				bool needStart = blobThread == null || blobThread.IsTerminated();
				if (needStart)
				{
					blobThread = new com.db4o.@internal.cs.BlobProcessor(this);
				}
				blobThread.Add(msg);
				if (needStart)
				{
					blobThread.Start();
				}
			}
		}

		public override void RaiseVersion(long a_minimumVersion)
		{
			WriteMsg(com.db4o.@internal.cs.messages.Msg.RAISE_VERSION.GetWriterForLong(i_trans
				, a_minimumVersion), true);
		}

		public override void ReadBytes(byte[] bytes, int address, int addressOffset, int 
			length)
		{
			throw com.db4o.@internal.Exceptions4.VirtualException();
		}

		public override void ReadBytes(byte[] a_bytes, int a_address, int a_length)
		{
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.READ_BYTES
				.GetWriterForInts(i_trans, new int[] { a_address, a_length });
			WriteMsg(msg, true);
			com.db4o.@internal.Buffer reader = ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg
				.READ_BYTES);
			System.Array.Copy(reader._buffer, 0, a_bytes, 0, a_length);
		}

		protected override bool Rename1(com.db4o.@internal.Config4Impl config)
		{
			LogMsg(58, null);
			return false;
		}

		public sealed override com.db4o.@internal.StatefulBuffer ReadWriterByID(com.db4o.@internal.Transaction
			 a_ta, int a_id)
		{
			try
			{
				com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.READ_OBJECT
					.GetWriterForInt(a_ta, a_id);
				WriteMsg(msg, true);
				com.db4o.@internal.StatefulBuffer bytes = ((com.db4o.@internal.cs.messages.MsgObject
					)ExpectedResponse(com.db4o.@internal.cs.messages.Msg.OBJECT_TO_CLIENT)).Unmarshall
					();
				if (bytes == null)
				{
					return null;
				}
				bytes.SetTransaction(a_ta);
				return bytes;
			}
			catch
			{
				return null;
			}
		}

		public sealed override com.db4o.@internal.StatefulBuffer[] ReadWritersByIDs(com.db4o.@internal.Transaction
			 a_ta, int[] ids)
		{
			try
			{
				com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.READ_MULTIPLE_OBJECTS
					.GetWriterForIntArray(a_ta, ids, ids.Length);
				WriteMsg(msg, true);
				com.db4o.@internal.cs.messages.MsgD message = (com.db4o.@internal.cs.messages.MsgD
					)ExpectedResponse(com.db4o.@internal.cs.messages.Msg.READ_MULTIPLE_OBJECTS);
				int count = message.ReadInt();
				com.db4o.@internal.StatefulBuffer[] yapWriters = new com.db4o.@internal.StatefulBuffer
					[count];
				for (int i = 0; i < count; i++)
				{
					com.db4o.@internal.cs.messages.MsgObject mso = (com.db4o.@internal.cs.messages.MsgObject
						)com.db4o.@internal.cs.messages.Msg.OBJECT_TO_CLIENT.Clone(GetTransaction());
					mso.PayLoad(message.PayLoad().ReadYapBytes());
					if (mso.PayLoad() != null)
					{
						mso.PayLoad().IncrementOffset(com.db4o.@internal.Const4.MESSAGE_LENGTH);
						yapWriters[i] = mso.Unmarshall(com.db4o.@internal.Const4.MESSAGE_LENGTH);
						yapWriters[i].SetTransaction(a_ta);
					}
				}
				return yapWriters;
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		public sealed override com.db4o.@internal.Buffer ReadReaderByID(com.db4o.@internal.Transaction
			 a_ta, int a_id)
		{
			return ReadWriterByID(a_ta, a_id);
		}

		private com.db4o.@internal.query.result.AbstractQueryResult ReadQueryResult(com.db4o.@internal.Transaction
			 trans)
		{
			com.db4o.@internal.query.result.AbstractQueryResult queryResult = null;
			com.db4o.@internal.Buffer reader = ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg
				.QUERY_RESULT);
			int queryResultID = reader.ReadInt();
			if (queryResultID > 0)
			{
				queryResult = new com.db4o.@internal.cs.LazyClientQueryResult(trans, this, queryResultID
					);
			}
			else
			{
				queryResult = new com.db4o.@internal.cs.ClientQueryResult(trans);
			}
			queryResult.LoadFromIdReader(reader);
			return queryResult;
		}

		internal virtual void ReadThis()
		{
			WriteMsg(com.db4o.@internal.cs.messages.Msg.GET_CLASSES.GetWriter(i_systemTrans), 
				true);
			com.db4o.@internal.Buffer bytes = ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg
				.GET_CLASSES);
			ClassCollection().SetID(bytes.ReadInt());
			CreateStringIO(bytes.ReadByte());
			ClassCollection().Read(i_systemTrans);
			ClassCollection().RefreshClasses();
		}

		public override void ReleaseSemaphore(string name)
		{
			lock (i_lock)
			{
				CheckClosed();
				if (name == null)
				{
					throw new System.ArgumentNullException();
				}
				WriteMsg(com.db4o.@internal.cs.messages.Msg.RELEASE_SEMAPHORE.GetWriterForString(
					i_trans, name), true);
			}
		}

		public override void ReleaseSemaphores(com.db4o.@internal.Transaction ta)
		{
		}

		private void ReReadAll(com.db4o.config.Configuration config)
		{
			remainingIDs = 0;
			Initialize1(config);
			InitializeTransactions();
			ReadThis();
		}

		public sealed override void Rollback1()
		{
			if (i_config.BatchMessages())
			{
				ClearBatchedObjects();
			}
			WriteMsg(com.db4o.@internal.cs.messages.Msg.ROLLBACK, true);
			i_trans.Rollback();
		}

		public override void Send(object obj)
		{
			lock (i_lock)
			{
				if (obj != null)
				{
					WriteMsg(com.db4o.@internal.cs.messages.Msg.USER_MESSAGE.GetWriter(Marshall(i_trans
						, obj)), true);
				}
			}
		}

		public sealed override void SetDirtyInSystemTransaction(com.db4o.@internal.PersistentBase
			 a_object)
		{
		}

		public override bool SetSemaphore(string name, int timeout)
		{
			lock (i_lock)
			{
				CheckClosed();
				if (name == null)
				{
					throw new System.ArgumentNullException();
				}
				com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.SET_SEMAPHORE
					.GetWriterForIntString(i_trans, timeout, name);
				WriteMsg(msg, true);
				com.db4o.@internal.cs.messages.Msg message = GetResponse();
				return (message.Equals(com.db4o.@internal.cs.messages.Msg.SUCCESS));
			}
		}

		public virtual void SwitchToFile(string fileName)
		{
			lock (i_lock)
			{
				Commit();
				com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.SWITCH_TO_FILE
					.GetWriterForString(i_trans, fileName);
				WriteMsg(msg, true);
				ExpectedResponse(com.db4o.@internal.cs.messages.Msg.OK);
				ReReadAll(com.db4o.Db4o.CloneConfiguration());
				switchedToFile = fileName;
			}
		}

		public virtual void SwitchToMainFile()
		{
			lock (i_lock)
			{
				Commit();
				WriteMsg(com.db4o.@internal.cs.messages.Msg.SWITCH_TO_MAIN_FILE, true);
				ExpectedResponse(com.db4o.@internal.cs.messages.Msg.OK);
				ReReadAll(com.db4o.Db4o.CloneConfiguration());
				switchedToFile = null;
			}
		}

		public virtual string Name()
		{
			return ToString();
		}

		public override string ToString()
		{
			return "Client Connection " + userName;
		}

		public override void Write(bool shuttingDown)
		{
		}

		public sealed override void WriteDirty()
		{
		}

		public sealed override void WriteEmbedded(com.db4o.@internal.StatefulBuffer a_parent
			, com.db4o.@internal.StatefulBuffer a_child)
		{
			a_parent.AddEmbedded(a_child);
		}

		internal void WriteMsg(com.db4o.@internal.cs.messages.Msg a_message)
		{
			a_message.Write(this, i_socket);
		}

		internal void WriteMsg(com.db4o.@internal.cs.messages.Msg a_message, bool flush)
		{
			if (i_config.BatchMessages())
			{
				if (flush && _batchedMessages.IsEmpty())
				{
					WriteMsg(a_message);
				}
				else
				{
					AddToBatch(a_message);
					if (flush || _batchedQueueLength > i_config.MaxBatchQueueSize())
					{
						WriteBatchedMessages();
					}
				}
			}
			else
			{
				WriteMsg(a_message);
			}
		}

		public sealed override void WriteNew(com.db4o.@internal.ClassMetadata a_yapClass, 
			com.db4o.@internal.StatefulBuffer aWriter)
		{
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.WRITE_NEW
				.GetWriter(a_yapClass, aWriter);
			WriteMsg(msg, false);
		}

		public sealed override void WriteTransactionPointer(int a_address)
		{
		}

		public sealed override void WriteUpdate(com.db4o.@internal.ClassMetadata a_yapClass
			, com.db4o.@internal.StatefulBuffer a_bytes)
		{
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.WRITE_UPDATE
				.GetWriter(a_yapClass, a_bytes);
			WriteMsg(msg, false);
		}

		public virtual bool IsAlive()
		{
			try
			{
				WriteMsg(com.db4o.@internal.cs.messages.Msg.PING, true);
				return ExpectedResponse(com.db4o.@internal.cs.messages.Msg.OK) != null;
			}
			catch (com.db4o.ext.Db4oException)
			{
				return false;
			}
		}

		public virtual com.db4o.foundation.network.Socket4 Socket()
		{
			return i_socket;
		}

		private void EnsureIDCacheAllocated(int prefetchIDCount)
		{
			if (_prefetchedIDs == null)
			{
				_prefetchedIDs = new int[prefetchIDCount];
				return;
			}
			if (prefetchIDCount > _prefetchedIDs.Length)
			{
				int[] newPrefetchedIDs = new int[prefetchIDCount];
				System.Array.Copy(_prefetchedIDs, 0, newPrefetchedIDs, 0, _prefetchedIDs.Length);
				_prefetchedIDs = newPrefetchedIDs;
			}
		}

		public override com.db4o.ext.SystemInfo SystemInfo()
		{
			throw new System.NotImplementedException("Functionality not availble on clients."
				);
		}

		public virtual void WriteBlobTo(com.db4o.@internal.Transaction trans, com.db4o.@internal.BlobImpl
			 blob, j4o.io.File file)
		{
			com.db4o.@internal.cs.messages.MsgBlob msg = (com.db4o.@internal.cs.messages.MsgBlob
				)com.db4o.@internal.cs.messages.Msg.READ_BLOB.GetWriterForInt(trans, (int)GetID(
				blob));
			msg._blob = blob;
			ProcessBlobMessage(msg);
		}

		public virtual void ReadBlobFrom(com.db4o.@internal.Transaction trans, com.db4o.@internal.BlobImpl
			 blob, j4o.io.File file)
		{
			com.db4o.@internal.cs.messages.MsgBlob msg = null;
			lock (Lock())
			{
				Set(blob);
				int id = (int)GetID(blob);
				msg = (com.db4o.@internal.cs.messages.MsgBlob)com.db4o.@internal.cs.messages.Msg.
					WRITE_BLOB.GetWriterForInt(trans, id);
				msg._blob = blob;
				blob.SetStatus(com.db4o.ext.Status.QUEUED);
			}
			ProcessBlobMessage(msg);
		}

		public override long[] GetIDsForClass(com.db4o.@internal.Transaction trans, com.db4o.@internal.ClassMetadata
			 clazz)
		{
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.GET_INTERNAL_IDS
				.GetWriterForInt(trans, clazz.GetID());
			WriteMsg(msg, true);
			com.db4o.@internal.Buffer reader = ExpectedByteResponse(com.db4o.@internal.cs.messages.Msg
				.ID_LIST);
			int size = reader.ReadInt();
			long[] ids = new long[size];
			for (int i = 0; i < size; i++)
			{
				ids[i] = reader.ReadInt();
			}
			return ids;
		}

		public override com.db4o.@internal.query.result.QueryResult ClassOnlyQuery(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.ClassMetadata clazz)
		{
			long[] ids = clazz.GetIDs(trans);
			com.db4o.@internal.cs.ClientQueryResult resClient = new com.db4o.@internal.cs.ClientQueryResult
				(trans, ids.Length);
			for (int i = 0; i < ids.Length; i++)
			{
				resClient.Add((int)ids[i]);
			}
			return resClient;
		}

		public override com.db4o.@internal.query.result.QueryResult ExecuteQuery(com.db4o.@internal.query.processor.QQuery
			 query)
		{
			com.db4o.@internal.Transaction trans = query.GetTransaction();
			query.EvaluationMode(Config().QueryEvaluationMode());
			query.Marshall();
			com.db4o.@internal.cs.messages.MsgD msg = com.db4o.@internal.cs.messages.Msg.QUERY_EXECUTE
				.GetWriter(Marshall(trans, query));
			WriteMsg(msg, true);
			return ReadQueryResult(trans);
		}

		public void WriteBatchedMessages()
		{
			if (_batchedMessages.IsEmpty())
			{
				return;
			}
			com.db4o.@internal.cs.messages.Msg msg;
			com.db4o.@internal.cs.messages.MsgD multibytes = com.db4o.@internal.cs.messages.Msg
				.WRITE_BATCHED_MESSAGES.GetWriterForLength(GetTransaction(), _batchedQueueLength
				);
			multibytes.WriteInt(_batchedMessages.Size());
			System.Collections.IEnumerator iter = _batchedMessages.GetEnumerator();
			while (iter.MoveNext())
			{
				msg = (com.db4o.@internal.cs.messages.Msg)iter.Current;
				if (msg == null)
				{
					multibytes.WriteInt(0);
				}
				else
				{
					multibytes.WriteInt(msg.PayLoad().GetLength());
					multibytes.PayLoad().Append(msg.PayLoad()._buffer);
				}
			}
			WriteMsg(multibytes);
			ClearBatchedObjects();
		}

		public void AddToBatch(com.db4o.@internal.cs.messages.Msg msg)
		{
			_batchedMessages.Add(msg);
			_batchedQueueLength += com.db4o.@internal.Const4.INT_LENGTH + msg.PayLoad().GetLength
				();
		}

		private void ClearBatchedObjects()
		{
			_batchedMessages.Clear();
			_batchedQueueLength = com.db4o.@internal.Const4.INT_LENGTH;
		}
	}
}
