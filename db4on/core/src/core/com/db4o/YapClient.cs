namespace com.db4o
{
	/// <exclude></exclude>
	public class YapClient : com.db4o.YapStream, com.db4o.ext.ExtClient
	{
		internal readonly object blobLock = new object();

		private com.db4o.YapClientBlobThread blobThread;

		private com.db4o.foundation.network.YapSocket i_socket;

		internal com.db4o.foundation.Queue4 messageQueue = new com.db4o.foundation.Queue4
			();

		internal readonly com.db4o.foundation.Lock4 messageQueueLock = new com.db4o.foundation.Lock4
			();

		private string password;

		internal int[] prefetchedIDs = new int[com.db4o.YapConst.PREFETCH_ID_COUNT];

		private com.db4o.YapClientThread _readerThread;

		internal int remainingIDs;

		private string switchedToFile;

		private bool _singleThreaded;

		private string userName;

		private com.db4o.ext.Db4oDatabase i_db;

		private bool _doFinalize = true;

		private int _blockSize = 1;

		private YapClient() : base(null)
		{
		}

		public YapClient(string fakeServerFile) : this()
		{
			lock (Lock())
			{
				_singleThreaded = ConfigImpl().SingleThreadedClient();
				throw new j4o.lang.RuntimeException("This constructor is for Debug.fakeServer use only."
					);
				Initialize3();
				com.db4o.Platform4.PostOpen(this);
			}
		}

		internal YapClient(com.db4o.foundation.network.YapSocket socket, string user, string
			 password_, bool login) : this()
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
					i_references.StopTimer();
					throw e;
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
			_readerThread = new com.db4o.YapClientThread(this, socket, messageQueue, messageQueueLock
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

		internal override bool Close2()
		{
			if (null != _readerThread && _readerThread.IsClosed())
			{
				return true;
			}
			try
			{
				com.db4o.Msg.COMMIT_OK.Write(this, i_socket);
				ExpectedResponse(com.db4o.Msg.OK);
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.CatchAll(e);
			}
			try
			{
				com.db4o.Msg.CLOSE.Write(this, i_socket);
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.CatchAll(e);
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
				com.db4o.inside.Exceptions4.CatchAll(e);
			}
			try
			{
				i_socket.Close();
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.CatchAll(e);
			}
			bool ret = base.Close2();
			return ret;
		}

		internal sealed override void Commit1()
		{
			i_trans.Commit();
		}

		internal virtual com.db4o.foundation.network.YapSocket CreateParalellSocket()
		{
			com.db4o.Msg.GET_THREAD_ID.Write(this, i_socket);
			int serverThreadID = ExpectedByteResponse(com.db4o.Msg.ID_LIST).ReadInt();
			com.db4o.foundation.network.YapSocket sock = i_socket.OpenParalellSocket();
			if (!(i_socket is com.db4o.foundation.network.YapSocketFake))
			{
				LoginToServer(sock);
			}
			if (switchedToFile != null)
			{
				com.db4o.MsgD message = com.db4o.Msg.SWITCH_TO_FILE.GetWriterForString(i_systemTrans
					, switchedToFile);
				message.Write(this, sock);
				if (!(com.db4o.Msg.OK.Equals(com.db4o.Msg.ReadMessage(i_systemTrans, sock))))
				{
					throw new System.IO.IOException(com.db4o.Messages.Get(42));
				}
			}
			com.db4o.Msg.USE_TRANSACTION.GetWriterForInt(i_trans, serverThreadID).Write(this, 
				sock);
			return sock;
		}

		internal sealed override com.db4o.QueryResultImpl CreateQResult(com.db4o.Transaction
			 a_ta)
		{
			return new com.db4o.QResultClient(a_ta);
		}

		public sealed override com.db4o.Transaction NewTransaction(com.db4o.Transaction parentTransaction
			)
		{
			return new com.db4o.TransactionClient(this, parentTransaction);
		}

		internal override bool CreateYapClass(com.db4o.YapClass a_yapClass, com.db4o.reflect.ReflectClass
			 a_class, com.db4o.YapClass a_superYapClass)
		{
			WriteMsg(com.db4o.Msg.CREATE_CLASS.GetWriterForString(i_systemTrans, a_class.GetName
				()));
			com.db4o.Msg resp = GetResponse();
			if (resp == null)
			{
				return false;
			}
			if (resp.Equals(com.db4o.Msg.FAILED))
			{
				if (ConfigImpl().ExceptionsOnNotStorable())
				{
					throw new com.db4o.ext.ObjectNotStorableException(a_class);
				}
				return false;
			}
			if (!resp.Equals(com.db4o.Msg.OBJECT_TO_CLIENT))
			{
				return false;
			}
			com.db4o.MsgObject message = (com.db4o.MsgObject)resp;
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
			a_yapClass.SetID(message._id);
			a_yapClass.ReadName1(GetSystemTransaction(), bytes);
			i_classCollection.AddYapClass(a_yapClass);
			i_classCollection.ReadYapClass(a_yapClass, a_class);
			return true;
		}

		public override long CurrentVersion()
		{
			WriteMsg(com.db4o.Msg.CURRENT_VERSION);
			return ((com.db4o.MsgD)ExpectedResponse(com.db4o.Msg.ID_LIST)).ReadLong();
		}

		internal sealed override bool Delete5(com.db4o.Transaction ta, com.db4o.YapObject
			 yo, int a_cascade, bool userCall)
		{
			WriteMsg(com.db4o.Msg.DELETE.GetWriterForInts(i_trans, new int[] { yo.GetID(), userCall
				 ? 1 : 0 }));
			return true;
		}

		internal override bool DetectSchemaChanges()
		{
			return false;
		}

		protected override bool DoFinalize()
		{
			return _doFinalize;
		}

		internal com.db4o.YapWriter ExpectedByteResponse(com.db4o.Msg expectedMessage)
		{
			com.db4o.Msg msg = ExpectedResponse(expectedMessage);
			if (msg == null)
			{
				return null;
			}
			return msg.GetByteLoad();
		}

		internal com.db4o.Msg ExpectedResponse(com.db4o.Msg expectedMessage)
		{
			com.db4o.Msg message = GetResponse();
			if (expectedMessage.Equals(message))
			{
				return message;
			}
			return null;
		}

		internal void Free(int a_address, int a_length)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		internal override void GetAll(com.db4o.Transaction ta, com.db4o.QueryResultImpl a_res
			)
		{
			WriteMsg(com.db4o.Msg.GET_ALL);
			ReadResult(a_res);
		}

		/// <summary>may return null, if no message is returned.</summary>
		/// <remarks>
		/// may return null, if no message is returned. Error handling is weak and
		/// should ideally be able to trigger some sort of state listener (connection
		/// dead) on the client.
		/// </remarks>
		internal virtual com.db4o.Msg GetResponse()
		{
			return _singleThreaded ? GetResponseSingleThreaded() : GetResponseMultiThreaded();
		}

		private com.db4o.Msg GetResponseMultiThreaded()
		{
			try
			{
				return (com.db4o.Msg)messageQueueLock.Run(new _AnonymousInnerClass308(this));
			}
			catch (System.Exception ex)
			{
				com.db4o.inside.Exceptions4.CatchAll(ex);
				return null;
			}
		}

		private sealed class _AnonymousInnerClass308 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass308(YapClient _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object Run()
			{
				com.db4o.Msg message = this.RetrieveMessage();
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
				if (this._enclosing._readerThread == null || this._enclosing._readerThread.IsClosed
					())
				{
					this._enclosing._doFinalize = false;
					com.db4o.inside.Exceptions4.ThrowRuntimeException(20, this._enclosing.Name());
				}
			}

			private com.db4o.Msg RetrieveMessage()
			{
				com.db4o.Msg message = null;
				message = (com.db4o.Msg)this._enclosing.messageQueue.Next();
				if (message != null)
				{
					if (com.db4o.Msg.ERROR.Equals(message))
					{
						throw new com.db4o.ext.Db4oException("Client connection error");
					}
				}
				return message;
			}

			private readonly YapClient _enclosing;
		}

		private com.db4o.Msg GetResponseSingleThreaded()
		{
			while (i_socket != null)
			{
				try
				{
					com.db4o.Msg message = com.db4o.Msg.ReadMessage(i_trans, i_socket);
					if (com.db4o.Msg.PING.Equals(message))
					{
						WriteMsg(com.db4o.Msg.OK);
					}
					else
					{
						if (com.db4o.Msg.CLOSE.Equals(message))
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
				catch (System.Exception e)
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
			WriteMsg(com.db4o.Msg.CLASS_NAME_FOR_ID.GetWriterForInt(i_systemTrans, a_id));
			com.db4o.MsgD message = (com.db4o.MsgD)ExpectedResponse(com.db4o.Msg.CLASS_NAME_FOR_ID
				);
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

		internal override bool HasShutDownHook()
		{
			return false;
		}

		public override com.db4o.ext.Db4oDatabase Identity()
		{
			if (i_db == null)
			{
				WriteMsg(com.db4o.Msg.IDENTITY);
				com.db4o.YapWriter reader = ExpectedByteResponse(com.db4o.Msg.ID_LIST);
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
				com.db4o.MsgD message = com.db4o.Msg.LOGIN.GetWriterForLength(i_systemTrans, length
					);
				message.WriteString(userName);
				message.WriteString(password);
				message.Write(this, a_socket);
				com.db4o.Msg msg = com.db4o.Msg.ReadMessage(i_systemTrans, a_socket);
				if (!com.db4o.Msg.LOGIN_OK.Equals(msg))
				{
					throw new System.IO.IOException(com.db4o.Messages.Get(42));
				}
				com.db4o.YapWriter payLoad = msg.GetPayLoad();
				_blockSize = payLoad.ReadInt();
				int doEncrypt = payLoad.ReadInt();
				if (doEncrypt == 0)
				{
					i_handlers.OldEncryptionOff();
				}
			}
		}

		internal override bool MaintainsIndices()
		{
			return false;
		}

		public sealed override int NewUserObject()
		{
			com.db4o.YapWriter reader = null;
			if (remainingIDs < 1)
			{
				WriteMsg(com.db4o.Msg.PREFETCH_IDS);
				reader = ExpectedByteResponse(com.db4o.Msg.ID_LIST);
				for (int i = com.db4o.YapConst.PREFETCH_ID_COUNT - 1; i >= 0; i--)
				{
					prefetchedIDs[i] = reader.ReadInt();
				}
				remainingIDs = com.db4o.YapConst.PREFETCH_ID_COUNT;
			}
			remainingIDs--;
			return prefetchedIDs[remainingIDs];
		}

		internal virtual int PrefetchObjects(com.db4o.QResultClient qResult, object[] prefetched
			, int prefetchCount)
		{
			int count = 0;
			int toGet = 0;
			int[] idsToGet = new int[prefetchCount];
			int[] position = new int[prefetchCount];
			while (qResult.HasNext() && (count < prefetchCount))
			{
				bool foundInCache = false;
				int id = qResult.NextInt();
				if (id > 0)
				{
					com.db4o.YapObject yo = GetYapObject(id);
					if (yo != null)
					{
						object candidate = yo.GetObject();
						if (candidate != null)
						{
							prefetched[count] = candidate;
							foundInCache = true;
						}
						else
						{
							YapObjectGCd(yo);
						}
					}
					if (!foundInCache)
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
				WriteMsg(com.db4o.Msg.READ_MULTIPLE_OBJECTS.GetWriterForIntArray(i_trans, idsToGet
					, toGet));
				com.db4o.MsgD message = (com.db4o.MsgD)ExpectedResponse(com.db4o.Msg.READ_MULTIPLE_OBJECTS
					);
				int embeddedMessageCount = message.ReadInt();
				for (int i = 0; i < embeddedMessageCount; i++)
				{
					com.db4o.MsgObject mso = (com.db4o.MsgObject)com.db4o.Msg.OBJECT_TO_CLIENT.Clone(
						qResult.i_trans);
					mso._payLoad = message._payLoad.ReadYapBytes();
					if (mso._payLoad != null)
					{
						mso._payLoad.IncrementOffset(com.db4o.YapConst.MESSAGE_LENGTH);
						com.db4o.YapWriter reader = mso.Unmarshall(com.db4o.YapConst.MESSAGE_LENGTH);
						prefetched[position[i]] = new com.db4o.YapObject(idsToGet[i]).ReadPrefetch(this, 
							qResult.i_trans, reader);
					}
				}
			}
			return count;
		}

		internal virtual void ProcessBlobMessage(com.db4o.MsgBlob msg)
		{
			lock (blobLock)
			{
				bool needStart = blobThread == null || blobThread.IsTerminated();
				if (needStart)
				{
					blobThread = new com.db4o.YapClientBlobThread(this);
				}
				blobThread.Add(msg);
				if (needStart)
				{
					blobThread.Start();
				}
			}
		}

		internal void QueryExecute(com.db4o.QQuery a_query, com.db4o.QueryResultImpl a_res
			)
		{
			WriteMsg(com.db4o.Msg.QUERY_EXECUTE.GetWriter(Marshall(a_query.GetTransaction(), 
				a_query)));
			ReadResult(a_res);
		}

		public override void RaiseVersion(long a_minimumVersion)
		{
			WriteMsg(com.db4o.Msg.RAISE_VERSION.GetWriterForLong(i_trans, a_minimumVersion));
		}

		internal override void ReadBytes(byte[] bytes, int address, int addressOffset, int
			 length)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		internal override void ReadBytes(byte[] a_bytes, int a_address, int a_length)
		{
			WriteMsg(com.db4o.Msg.READ_BYTES.GetWriterForInts(i_trans, new int[] { a_address, 
				a_length }));
			com.db4o.YapWriter reader = ExpectedByteResponse(com.db4o.Msg.READ_BYTES);
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
				WriteMsg(com.db4o.Msg.READ_OBJECT.GetWriterForInt(a_ta, a_id));
				com.db4o.YapWriter bytes = ((com.db4o.MsgObject)ExpectedResponse(com.db4o.Msg.OBJECT_TO_CLIENT
					)).Unmarshall();
				if (bytes == null)
				{
					return null;
				}
				bytes.SetTransaction(a_ta);
				return bytes;
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		public sealed override com.db4o.YapReader ReadReaderByID(com.db4o.Transaction a_ta
			, int a_id)
		{
			return ReadWriterByID(a_ta, a_id);
		}

		private void ReadResult(com.db4o.QueryResultImpl aRes)
		{
			com.db4o.YapWriter reader = ExpectedByteResponse(com.db4o.Msg.ID_LIST);
			int size = reader.ReadInt();
			for (int i = 0; i < size; i++)
			{
				aRes.Add(reader.ReadInt());
			}
			aRes.Reset();
		}

		internal virtual void ReadThis()
		{
			WriteMsg(com.db4o.Msg.GET_CLASSES.GetWriter(i_systemTrans));
			com.db4o.YapWriter bytes = ExpectedByteResponse(com.db4o.Msg.GET_CLASSES);
			i_classCollection.SetID(bytes.ReadInt());
			CreateStringIO(bytes.ReadByte());
			i_classCollection.Read(i_systemTrans);
			i_classCollection.RefreshClasses();
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
				WriteMsg(com.db4o.Msg.RELEASE_SEMAPHORE.GetWriterForString(i_trans, name));
			}
		}

		internal override void ReleaseSemaphores(com.db4o.Transaction ta)
		{
		}

		private void ReReadAll()
		{
			remainingIDs = 0;
			Initialize0();
			Initialize1();
			InitializeTransactions();
			ReadThis();
		}

		internal sealed override void Rollback1()
		{
			WriteMsg(com.db4o.Msg.ROLLBACK);
			i_trans.Rollback();
		}

		public override void Send(object obj)
		{
			lock (i_lock)
			{
				if (obj != null)
				{
					WriteMsg(com.db4o.Msg.USER_MESSAGE.GetWriter(Marshall(i_trans, obj)));
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
				WriteMsg(com.db4o.Msg.SET_SEMAPHORE.GetWriterForIntString(i_trans, timeout, name)
					);
				com.db4o.Msg message = GetResponse();
				return (message.Equals(com.db4o.Msg.SUCCESS));
			}
		}

		public virtual void SwitchToFile(string fileName)
		{
			lock (i_lock)
			{
				Commit();
				WriteMsg(com.db4o.Msg.SWITCH_TO_FILE.GetWriterForString(i_trans, fileName));
				ExpectedResponse(com.db4o.Msg.OK);
				ReReadAll();
				switchedToFile = fileName;
			}
		}

		public virtual void SwitchToMainFile()
		{
			lock (i_lock)
			{
				Commit();
				WriteMsg(com.db4o.Msg.SWITCH_TO_MAIN_FILE);
				ExpectedResponse(com.db4o.Msg.OK);
				ReReadAll();
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

		internal override void Write(bool shuttingDown)
		{
		}

		internal sealed override void WriteDirty()
		{
		}

		public sealed override void WriteEmbedded(com.db4o.YapWriter a_parent, com.db4o.YapWriter
			 a_child)
		{
			a_parent.AddEmbedded(a_child);
		}

		internal void WriteMsg(com.db4o.Msg a_message)
		{
			a_message.Write(this, i_socket);
		}

		public sealed override void WriteNew(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 aWriter)
		{
			WriteMsg(com.db4o.Msg.WRITE_NEW.GetWriter(a_yapClass, aWriter));
		}

		internal void WriteObject(com.db4o.YapMeta a_object, com.db4o.YapReader a_writer, 
			int address)
		{
			com.db4o.inside.Exceptions4.ShouldNeverBeCalled();
		}

		internal sealed override void WriteTransactionPointer(int a_address)
		{
		}

		public sealed override void WriteUpdate(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 a_bytes)
		{
			WriteMsg(com.db4o.Msg.WRITE_UPDATE.GetWriter(a_yapClass, a_bytes));
		}

		public virtual bool IsAlive()
		{
			try
			{
				WriteMsg(com.db4o.Msg.PING);
				return ExpectedResponse(com.db4o.Msg.OK) != null;
			}
			catch (com.db4o.ext.Db4oException exc)
			{
				return false;
			}
		}

		public virtual com.db4o.foundation.network.YapSocket Socket()
		{
			return i_socket;
		}
	}
}
