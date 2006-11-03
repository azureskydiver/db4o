namespace com.db4o.cs
{
	/// <exclude></exclude>
	public class YapClient : com.db4o.YapStream, com.db4o.ext.ExtClient, com.db4o.BlobTransport
	{
		internal readonly object blobLock = new object();

		private com.db4o.cs.YapClientBlobThread blobThread;

		private com.db4o.foundation.network.YapSocket i_socket;

		internal com.db4o.foundation.Queue4 messageQueue = new com.db4o.foundation.Queue4
			();

		internal readonly com.db4o.foundation.Lock4 messageQueueLock = new com.db4o.foundation.Lock4
			();

		private string password;

		internal int[] _prefetchedIDs;

		private com.db4o.cs.YapClientThread _readerThread;

		internal int remainingIDs;

		private string switchedToFile;

		private bool _singleThreaded;

		private string userName;

		private com.db4o.ext.Db4oDatabase i_db;

		protected bool _doFinalize = true;

		private int _blockSize = 1;

		private YapClient(com.db4o.config.Configuration config) : base(config, null)
		{
		}

		public YapClient(string fakeServerFile) : this(com.db4o.Db4o.CloneConfiguration()
			)
		{
			lock (Lock())
			{
				_singleThreaded = ConfigImpl().SingleThreadedClient();
				throw new System.Exception("This constructor is for Debug.fakeServer use only.");
				Initialize3();
				com.db4o.Platform4.PostOpen(this);
			}
		}

		public YapClient(com.db4o.config.Configuration config, com.db4o.foundation.network.YapSocket
			 socket, string user, string password_, bool login) : this(config)
		{
			lock (Lock())
			{
				_singleThreaded = ConfigImpl().SingleThreadedClient();
				if (password_ == null)
				{
					throw new System.ArgumentNullException(com.db4o.Messages.Get(56));
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
				com.db4o.Platform4.PostOpen(this);
			}
		}

		private void StartReaderThread(com.db4o.foundation.network.YapSocket socket, string
			 user)
		{
			_readerThread = new com.db4o.cs.YapClientThread(this, socket, messageQueue, messageQueueLock
				);
			_readerThread.SetName("db4o message client for user " + user);
			_readerThread.Start();
		}

		public override void Backup(string path)
		{
			com.db4o.inside.Exceptions4.ThrowRuntimeException(60);
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
			if (_readerThread.IsClosed())
			{
				return base.Close2();
			}
			try
			{
				com.db4o.cs.messages.Msg.COMMIT_OK.Write(this, i_socket);
				ExpectedResponse(com.db4o.cs.messages.Msg.OK);
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.CatchAllExceptDb4oException(e);
			}
			try
			{
				com.db4o.cs.messages.Msg.CLOSE.Write(this, i_socket);
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.CatchAllExceptDb4oException(e);
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
				com.db4o.inside.Exceptions4.CatchAllExceptDb4oException(e);
			}
			try
			{
				i_socket.Close();
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.CatchAllExceptDb4oException(e);
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
			return com.db4o.inside.convert.Converter.VERSION;
		}

		internal virtual com.db4o.foundation.network.YapSocket CreateParalellSocket()
		{
			com.db4o.cs.messages.Msg.GET_THREAD_ID.Write(this, i_socket);
			int serverThreadID = ExpectedByteResponse(com.db4o.cs.messages.Msg.ID_LIST).ReadInt
				();
			com.db4o.foundation.network.YapSocket sock = i_socket.OpenParalellSocket();
			if (!(i_socket is com.db4o.foundation.network.YapSocketFake))
			{
				LoginToServer(sock);
			}
			if (switchedToFile != null)
			{
				com.db4o.cs.messages.MsgD message = com.db4o.cs.messages.Msg.SWITCH_TO_FILE.GetWriterForString
					(i_systemTrans, switchedToFile);
				message.Write(this, sock);
				if (!(com.db4o.cs.messages.Msg.OK.Equals(com.db4o.cs.messages.Msg.ReadMessage(i_systemTrans
					, sock))))
				{
					throw new System.IO.IOException(com.db4o.Messages.Get(42));
				}
			}
			com.db4o.cs.messages.Msg.USE_TRANSACTION.GetWriterForInt(i_trans, serverThreadID)
				.Write(this, sock);
			return sock;
		}

		public sealed override com.db4o.inside.query.QueryResult NewQueryResult(com.db4o.Transaction
			 a_ta)
		{
			return new com.db4o.cs.ClientQueryResult(a_ta);
		}

		public sealed override com.db4o.Transaction NewTransaction(com.db4o.Transaction parentTransaction
			)
		{
			return new com.db4o.cs.TransactionClient(this, parentTransaction);
		}

		public override bool CreateYapClass(com.db4o.YapClass a_yapClass, com.db4o.reflect.ReflectClass
			 a_class, com.db4o.YapClass a_superYapClass)
		{
			WriteMsg(com.db4o.cs.messages.Msg.CREATE_CLASS.GetWriterForString(i_systemTrans, 
				a_class.GetName()));
			com.db4o.cs.messages.Msg resp = GetResponse();
			if (resp == null)
			{
				return false;
			}
			if (resp.Equals(com.db4o.cs.messages.Msg.FAILED))
			{
				if (ConfigImpl().ExceptionsOnNotStorable())
				{
					throw new com.db4o.ext.ObjectNotStorableException(a_class);
				}
				return false;
			}
			if (!resp.Equals(com.db4o.cs.messages.Msg.OBJECT_TO_CLIENT))
			{
				return false;
			}
			com.db4o.cs.messages.MsgObject message = (com.db4o.cs.messages.MsgObject)resp;
			com.db4o.YapWriter bytes = message.Unmarshall();
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

		public override long CurrentVersion()
		{
			WriteMsg(com.db4o.cs.messages.Msg.CURRENT_VERSION);
			return ((com.db4o.cs.messages.MsgD)ExpectedResponse(com.db4o.cs.messages.Msg.ID_LIST
				)).ReadLong();
		}

		public sealed override bool Delete5(com.db4o.Transaction ta, com.db4o.YapObject yo
			, int a_cascade, bool userCall)
		{
			WriteMsg(com.db4o.cs.messages.Msg.DELETE.GetWriterForInts(i_trans, new int[] { yo
				.GetID(), userCall ? 1 : 0 }));
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

		internal com.db4o.YapReader ExpectedByteResponse(com.db4o.cs.messages.Msg expectedMessage
			)
		{
			com.db4o.cs.messages.Msg msg = ExpectedResponse(expectedMessage);
			if (msg == null)
			{
				return null;
			}
			return msg.GetByteLoad();
		}

		internal com.db4o.cs.messages.Msg ExpectedResponse(com.db4o.cs.messages.Msg expectedMessage
			)
		{
			com.db4o.cs.messages.Msg message = GetResponse();
			if (expectedMessage.Equals(message))
			{
				return message;
			}
			return null;
		}

		public override com.db4o.inside.query.QueryResult GetAll(com.db4o.Transaction ta)
		{
			WriteMsg(com.db4o.cs.messages.Msg.GET_ALL);
			com.db4o.inside.query.QueryResult queryResult = NewQueryResult(ta);
			ReadResult(queryResult);
			return queryResult;
		}

		/// <summary>may return null, if no message is returned.</summary>
		/// <remarks>
		/// may return null, if no message is returned. Error handling is weak and
		/// should ideally be able to trigger some sort of state listener (connection
		/// dead) on the client.
		/// </remarks>
		internal virtual com.db4o.cs.messages.Msg GetResponse()
		{
			return _singleThreaded ? GetResponseSingleThreaded() : GetResponseMultiThreaded();
		}

		private com.db4o.cs.messages.Msg GetResponseMultiThreaded()
		{
			try
			{
				return (com.db4o.cs.messages.Msg)messageQueueLock.Run(new _AnonymousInnerClass315
					(this));
			}
			catch (System.Exception ex)
			{
				com.db4o.inside.Exceptions4.CatchAllExceptDb4oException(ex);
				return null;
			}
		}

		private sealed class _AnonymousInnerClass315 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass315(YapClient _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object Run()
			{
				com.db4o.cs.messages.Msg message = this.RetrieveMessage();
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
					throw new com.db4o.ext.Db4oException(com.db4o.Messages.Get(com.db4o.Messages.CLOSED_OR_OPEN_FAILED
						));
				}
			}

			private com.db4o.cs.messages.Msg RetrieveMessage()
			{
				com.db4o.cs.messages.Msg message = null;
				message = (com.db4o.cs.messages.Msg)this._enclosing.messageQueue.Next();
				if (message != null)
				{
					if (com.db4o.cs.messages.Msg.ERROR.Equals(message))
					{
						throw new com.db4o.ext.Db4oException("Client connection error");
					}
				}
				return message;
			}

			private readonly YapClient _enclosing;
		}

		private com.db4o.cs.messages.Msg GetResponseSingleThreaded()
		{
			while (i_socket != null)
			{
				try
				{
					com.db4o.cs.messages.Msg message = com.db4o.cs.messages.Msg.ReadMessage(i_trans, 
						i_socket);
					if (com.db4o.cs.messages.Msg.PING.Equals(message))
					{
						WriteMsg(com.db4o.cs.messages.Msg.OK);
					}
					else
					{
						if (com.db4o.cs.messages.Msg.CLOSE.Equals(message))
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

		public override com.db4o.YapClass GetYapClass(int a_id)
		{
			com.db4o.YapClass yc = base.GetYapClass(a_id);
			if (yc != null)
			{
				return yc;
			}
			WriteMsg(com.db4o.cs.messages.Msg.CLASS_NAME_FOR_ID.GetWriterForInt(i_systemTrans
				, a_id));
			com.db4o.cs.messages.MsgD message = (com.db4o.cs.messages.MsgD)ExpectedResponse(com.db4o.cs.messages.Msg
				.CLASS_NAME_FOR_ID);
			string className = message.ReadString();
			if (className != null && className.Length > 0)
			{
				com.db4o.reflect.ReflectClass claxx = Reflector().ForName(className);
				if (claxx != null)
				{
					return GetYapClass(claxx, true);
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
				WriteMsg(com.db4o.cs.messages.Msg.IDENTITY);
				com.db4o.YapReader reader = ExpectedByteResponse(com.db4o.cs.messages.Msg.ID_LIST
					);
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

		internal virtual void LoginToServer(com.db4o.foundation.network.YapSocket a_socket
			)
		{
			if (password != null)
			{
				com.db4o.YapStringIOUnicode stringWriter = new com.db4o.YapStringIOUnicode();
				int length = stringWriter.Length(userName) + stringWriter.Length(password);
				com.db4o.cs.messages.MsgD message = com.db4o.cs.messages.Msg.LOGIN.GetWriterForLength
					(i_systemTrans, length);
				message.WriteString(userName);
				message.WriteString(password);
				message.Write(this, a_socket);
				com.db4o.cs.messages.Msg msg = com.db4o.cs.messages.Msg.ReadMessage(i_systemTrans
					, a_socket);
				if (!com.db4o.cs.messages.Msg.LOGIN_OK.Equals(msg))
				{
					throw new System.IO.IOException(com.db4o.Messages.Get(42));
				}
				com.db4o.YapReader payLoad = msg.PayLoad();
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
			com.db4o.YapReader reader = null;
			if (remainingIDs < 1)
			{
				WriteMsg(com.db4o.cs.messages.Msg.PREFETCH_IDS.GetWriterForInt(i_trans, prefetchIDCount
					));
				reader = ExpectedByteResponse(com.db4o.cs.messages.Msg.ID_LIST);
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
				WriteMsg(com.db4o.cs.messages.Msg.READ_MULTIPLE_OBJECTS.GetWriterForIntArray(i_trans
					, idsToGet, toGet));
				com.db4o.cs.messages.MsgD message = (com.db4o.cs.messages.MsgD)ExpectedResponse(com.db4o.cs.messages.Msg
					.READ_MULTIPLE_OBJECTS);
				int embeddedMessageCount = message.ReadInt();
				for (int i = 0; i < embeddedMessageCount; i++)
				{
					com.db4o.cs.messages.MsgObject mso = (com.db4o.cs.messages.MsgObject)com.db4o.cs.messages.Msg
						.OBJECT_TO_CLIENT.Clone(GetTransaction());
					mso.PayLoad(message.PayLoad().ReadYapBytes());
					if (mso.PayLoad() != null)
					{
						mso.PayLoad().IncrementOffset(com.db4o.YapConst.MESSAGE_LENGTH);
						com.db4o.YapWriter reader = mso.Unmarshall(com.db4o.YapConst.MESSAGE_LENGTH);
						object obj = ObjectForIDFromCache(idsToGet[i]);
						if (obj != null)
						{
							prefetched[position[i]] = obj;
						}
						else
						{
							prefetched[position[i]] = new com.db4o.YapObject(idsToGet[i]).ReadPrefetch(this, 
								reader);
						}
					}
				}
			}
			return count;
		}

		internal virtual void ProcessBlobMessage(com.db4o.cs.messages.MsgBlob msg)
		{
			lock (blobLock)
			{
				bool needStart = blobThread == null || blobThread.IsTerminated();
				if (needStart)
				{
					blobThread = new com.db4o.cs.YapClientBlobThread(this);
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
			WriteMsg(com.db4o.cs.messages.Msg.RAISE_VERSION.GetWriterForLong(i_trans, a_minimumVersion
				));
		}

		public override void ReadBytes(byte[] bytes, int address, int addressOffset, int 
			length)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public override void ReadBytes(byte[] a_bytes, int a_address, int a_length)
		{
			WriteMsg(com.db4o.cs.messages.Msg.READ_BYTES.GetWriterForInts(i_trans, new int[] 
				{ a_address, a_length }));
			com.db4o.YapReader reader = ExpectedByteResponse(com.db4o.cs.messages.Msg.READ_BYTES
				);
			System.Array.Copy(reader._buffer, 0, a_bytes, 0, a_length);
		}

		protected override bool Rename1(com.db4o.Config4Impl config)
		{
			LogMsg(58, null);
			return false;
		}

		public sealed override com.db4o.YapWriter ReadWriterByID(com.db4o.Transaction a_ta
			, int a_id)
		{
			try
			{
				WriteMsg(com.db4o.cs.messages.Msg.READ_OBJECT.GetWriterForInt(a_ta, a_id));
				com.db4o.YapWriter bytes = ((com.db4o.cs.messages.MsgObject)ExpectedResponse(com.db4o.cs.messages.Msg
					.OBJECT_TO_CLIENT)).Unmarshall();
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

		public sealed override com.db4o.YapReader ReadReaderByID(com.db4o.Transaction a_ta
			, int a_id)
		{
			return ReadWriterByID(a_ta, a_id);
		}

		private void ReadResult(com.db4o.inside.query.QueryResult queryResult)
		{
			com.db4o.YapReader reader = ExpectedByteResponse(com.db4o.cs.messages.Msg.ID_LIST
				);
			queryResult.LoadFromIdReader(reader);
		}

		internal virtual void ReadThis()
		{
			WriteMsg(com.db4o.cs.messages.Msg.GET_CLASSES.GetWriter(i_systemTrans));
			com.db4o.YapReader bytes = ExpectedByteResponse(com.db4o.cs.messages.Msg.GET_CLASSES
				);
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
				WriteMsg(com.db4o.cs.messages.Msg.RELEASE_SEMAPHORE.GetWriterForString(i_trans, name
					));
			}
		}

		public override void ReleaseSemaphores(com.db4o.Transaction ta)
		{
		}

		private void ReReadAll(com.db4o.config.Configuration config)
		{
			remainingIDs = 0;
			Initialize0();
			Initialize1(config);
			InitializeTransactions();
			ReadThis();
		}

		public sealed override void Rollback1()
		{
			WriteMsg(com.db4o.cs.messages.Msg.ROLLBACK);
			i_trans.Rollback();
		}

		public override void Send(object obj)
		{
			lock (i_lock)
			{
				if (obj != null)
				{
					WriteMsg(com.db4o.cs.messages.Msg.USER_MESSAGE.GetWriter(Marshall(i_trans, obj)));
				}
			}
		}

		public sealed override void SetDirtyInSystemTransaction(com.db4o.YapMeta a_object
			)
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
				WriteMsg(com.db4o.cs.messages.Msg.SET_SEMAPHORE.GetWriterForIntString(i_trans, timeout
					, name));
				com.db4o.cs.messages.Msg message = GetResponse();
				return (message.Equals(com.db4o.cs.messages.Msg.SUCCESS));
			}
		}

		public virtual void SwitchToFile(string fileName)
		{
			lock (i_lock)
			{
				Commit();
				WriteMsg(com.db4o.cs.messages.Msg.SWITCH_TO_FILE.GetWriterForString(i_trans, fileName
					));
				ExpectedResponse(com.db4o.cs.messages.Msg.OK);
				ReReadAll(com.db4o.Db4o.CloneConfiguration());
				switchedToFile = fileName;
			}
		}

		public virtual void SwitchToMainFile()
		{
			lock (i_lock)
			{
				Commit();
				WriteMsg(com.db4o.cs.messages.Msg.SWITCH_TO_MAIN_FILE);
				ExpectedResponse(com.db4o.cs.messages.Msg.OK);
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

		public sealed override void WriteEmbedded(com.db4o.YapWriter a_parent, com.db4o.YapWriter
			 a_child)
		{
			a_parent.AddEmbedded(a_child);
		}

		internal void WriteMsg(com.db4o.cs.messages.Msg a_message)
		{
			a_message.Write(this, i_socket);
		}

		public sealed override void WriteNew(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 aWriter)
		{
			WriteMsg(com.db4o.cs.messages.Msg.WRITE_NEW.GetWriter(a_yapClass, aWriter));
		}

		public sealed override void WriteTransactionPointer(int a_address)
		{
		}

		public sealed override void WriteUpdate(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 a_bytes)
		{
			WriteMsg(com.db4o.cs.messages.Msg.WRITE_UPDATE.GetWriter(a_yapClass, a_bytes));
		}

		public virtual bool IsAlive()
		{
			try
			{
				WriteMsg(com.db4o.cs.messages.Msg.PING);
				return ExpectedResponse(com.db4o.cs.messages.Msg.OK) != null;
			}
			catch (com.db4o.ext.Db4oException)
			{
				return false;
			}
		}

		public virtual com.db4o.foundation.network.YapSocket Socket()
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

		public virtual void WriteBlobTo(com.db4o.Transaction trans, com.db4o.BlobImpl blob
			, j4o.io.File file)
		{
			com.db4o.cs.messages.MsgBlob msg = (com.db4o.cs.messages.MsgBlob)com.db4o.cs.messages.Msg
				.READ_BLOB.GetWriterForInt(trans, (int)GetID(blob));
			msg._blob = blob;
			ProcessBlobMessage(msg);
		}

		public virtual void ReadBlobFrom(com.db4o.Transaction trans, com.db4o.BlobImpl blob
			, j4o.io.File file)
		{
			com.db4o.cs.messages.MsgBlob msg = null;
			lock (Lock())
			{
				Set(blob);
				int id = (int)GetID(blob);
				msg = (com.db4o.cs.messages.MsgBlob)com.db4o.cs.messages.Msg.WRITE_BLOB.GetWriterForInt
					(trans, id);
				msg._blob = blob;
				blob.SetStatus(com.db4o.ext.Status.QUEUED);
			}
			ProcessBlobMessage(msg);
		}

		public override long[] GetIDsForClass(com.db4o.Transaction trans, com.db4o.YapClass
			 clazz)
		{
			WriteMsg(com.db4o.cs.messages.Msg.GET_INTERNAL_IDS.GetWriterForInt(trans, clazz.GetID
				()));
			com.db4o.YapReader reader = ExpectedByteResponse(com.db4o.cs.messages.Msg.ID_LIST
				);
			int size = reader.ReadInt();
			long[] ids = new long[size];
			for (int i = 0; i < size; i++)
			{
				ids[i] = reader.ReadInt();
			}
			return ids;
		}

		public override com.db4o.inside.query.QueryResult ClassOnlyQuery(com.db4o.Transaction
			 trans, com.db4o.YapClass clazz)
		{
			long[] ids = clazz.GetIDs(trans);
			com.db4o.cs.ClientQueryResult resClient = new com.db4o.cs.ClientQueryResult(trans
				, ids.Length);
			for (int i = 0; i < ids.Length; i++)
			{
				resClient.Add((int)ids[i]);
			}
			return resClient;
		}

		public override com.db4o.inside.query.QueryResult ExecuteQuery(com.db4o.QQuery query
			)
		{
			com.db4o.Transaction trans = query.GetTransaction();
			com.db4o.inside.query.QueryResult result = NewQueryResult(trans);
			query.Marshall();
			WriteMsg(com.db4o.cs.messages.Msg.QUERY_EXECUTE.GetWriter(Marshall(trans, query))
				);
			ReadResult(result);
			return result;
		}
	}
}
