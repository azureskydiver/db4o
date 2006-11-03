namespace com.db4o.cs.messages
{
	/// <summary>Messages for Client/Server Communication</summary>
	public class Msg : j4o.lang.Cloneable
	{
		internal static int _idGenerator = 1;

		private static com.db4o.cs.messages.Msg[] _messages = new com.db4o.cs.messages.Msg
			[60];

		internal int _msgID;

		internal string _name;

		internal com.db4o.Transaction _trans;

		public static readonly com.db4o.cs.messages.MsgD CLASS_NAME_FOR_ID = new com.db4o.cs.messages.MClassNameForID
			();

		public static readonly com.db4o.cs.messages.Msg CLOSE = new com.db4o.cs.messages.Msg
			("CLOSE");

		public static readonly com.db4o.cs.messages.Msg COMMIT = new com.db4o.cs.messages.MCommit
			();

		public static readonly com.db4o.cs.messages.Msg COMMIT_OK = new com.db4o.cs.messages.MCommitOK
			();

		public static readonly com.db4o.cs.messages.MsgD CREATE_CLASS = new com.db4o.cs.messages.MCreateClass
			();

		public static readonly com.db4o.cs.messages.Msg CURRENT_VERSION = new com.db4o.cs.messages.Msg
			("VERSION");

		public static readonly com.db4o.cs.messages.MsgD DELETE = new com.db4o.cs.messages.MDelete
			();

		public static readonly com.db4o.cs.messages.Msg ERROR = new com.db4o.cs.messages.Msg
			("ERROR");

		public static readonly com.db4o.cs.messages.Msg FAILED = new com.db4o.cs.messages.Msg
			("FAILED");

		public static readonly com.db4o.cs.messages.Msg GET_ALL = new com.db4o.cs.messages.MGetAll
			();

		public static readonly com.db4o.cs.messages.MsgD GET_CLASSES = new com.db4o.cs.messages.MGetClasses
			();

		public static readonly com.db4o.cs.messages.MsgD GET_INTERNAL_IDS = new com.db4o.cs.messages.MGetInternalIDs
			();

		public static readonly com.db4o.cs.messages.Msg GET_THREAD_ID = new com.db4o.cs.messages.Msg
			("GET_THREAD_ID");

		public static readonly com.db4o.cs.messages.MsgD ID_LIST = new com.db4o.cs.messages.MsgD
			("ID_LIST");

		public static readonly com.db4o.cs.messages.Msg IDENTITY = new com.db4o.cs.messages.Msg
			("IDENTITY");

		public static readonly com.db4o.cs.messages.MsgD LENGTH = new com.db4o.cs.messages.MsgD
			("LENGTH");

		public static readonly com.db4o.cs.messages.MsgD LOGIN = new com.db4o.cs.messages.MsgD
			("LOGIN");

		public static readonly com.db4o.cs.messages.MsgD LOGIN_OK = new com.db4o.cs.messages.MsgD
			("LOGIN_OK");

		public static readonly com.db4o.cs.messages.Msg NULL = new com.db4o.cs.messages.Msg
			("NULL");

		public static readonly com.db4o.cs.messages.MsgD OBJECT_BY_UUID = new com.db4o.cs.messages.MObjectByUuid
			();

		public static readonly com.db4o.cs.messages.MsgObject OBJECT_TO_CLIENT = new com.db4o.cs.messages.MsgObject
			();

		public static readonly com.db4o.cs.messages.Msg OK = new com.db4o.cs.messages.Msg
			("OK");

		public static readonly com.db4o.cs.messages.Msg PING = new com.db4o.cs.messages.Msg
			("PING");

		public static readonly com.db4o.cs.messages.MsgD PREFETCH_IDS = new com.db4o.cs.messages.MPrefetchIDs
			();

		public static readonly com.db4o.cs.messages.MsgObject QUERY_EXECUTE = new com.db4o.cs.messages.MQueryExecute
			();

		public static readonly com.db4o.cs.messages.MsgD RAISE_VERSION = new com.db4o.cs.messages.MsgD
			("RAISE_VERSION");

		public static readonly com.db4o.cs.messages.MsgBlob READ_BLOB = new com.db4o.cs.messages.MReadBlob
			();

		public static readonly com.db4o.cs.messages.MsgD READ_BYTES = new com.db4o.cs.messages.MReadBytes
			();

		public static readonly com.db4o.cs.messages.MsgD READ_MULTIPLE_OBJECTS = new com.db4o.cs.messages.MReadMultipleObjects
			();

		public static readonly com.db4o.cs.messages.MsgD READ_OBJECT = new com.db4o.cs.messages.MReadObject
			();

		public static readonly com.db4o.cs.messages.MsgD RELEASE_SEMAPHORE = new com.db4o.cs.messages.MReleaseSemaphore
			();

		public static readonly com.db4o.cs.messages.Msg ROLLBACK = new com.db4o.cs.messages.MRollback
			();

		public static readonly com.db4o.cs.messages.MsgD SET_SEMAPHORE = new com.db4o.cs.messages.MSetSemaphore
			();

		public static readonly com.db4o.cs.messages.Msg SUCCESS = new com.db4o.cs.messages.Msg
			("SUCCESS");

		public static readonly com.db4o.cs.messages.MsgD SWITCH_TO_FILE = new com.db4o.cs.messages.MsgD
			("SWITCH_F");

		public static readonly com.db4o.cs.messages.Msg SWITCH_TO_MAIN_FILE = new com.db4o.cs.messages.Msg
			("SWITCH_M");

		public static readonly com.db4o.cs.messages.Msg TA_BEGIN_END_SET = new com.db4o.cs.messages.MTaBeginEndSet
			();

		public static readonly com.db4o.cs.messages.MsgD TA_DELETE = new com.db4o.cs.messages.MTaDelete
			();

		public static readonly com.db4o.cs.messages.MsgD TA_DONT_DELETE = new com.db4o.cs.messages.MTaDontDelete
			();

		public static readonly com.db4o.cs.messages.MsgD TA_IS_DELETED = new com.db4o.cs.messages.MTaIsDeleted
			();

		public static readonly com.db4o.cs.messages.MsgD USER_MESSAGE = new com.db4o.cs.messages.MUserMessage
			();

		public static readonly com.db4o.cs.messages.MsgD USE_TRANSACTION = new com.db4o.cs.messages.MUseTransaction
			();

		public static readonly com.db4o.cs.messages.MsgBlob WRITE_BLOB = new com.db4o.cs.messages.MWriteBlob
			();

		public static readonly com.db4o.cs.messages.MWriteNew WRITE_NEW = new com.db4o.cs.messages.MWriteNew
			();

		public static readonly com.db4o.cs.messages.MsgObject WRITE_UPDATE = new com.db4o.cs.messages.MWriteUpdate
			();

		public static readonly com.db4o.cs.messages.MsgD WRITE_UPDATE_DELETE_MEMBERS = new 
			com.db4o.cs.messages.MWriteUpdateDeleteMembers();

		internal Msg()
		{
			_msgID = _idGenerator++;
			_messages[_msgID] = this;
		}

		internal Msg(string aName) : this()
		{
			_name = aName;
		}

		public com.db4o.cs.messages.Msg Clone(com.db4o.Transaction a_trans)
		{
			com.db4o.cs.messages.Msg msg = null;
			try
			{
				msg = (com.db4o.cs.messages.Msg)MemberwiseClone();
				msg._trans = a_trans;
			}
			catch (j4o.lang.CloneNotSupportedException)
			{
			}
			return msg;
		}

		public sealed override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null || j4o.lang.JavaSystem.GetClassForObject(obj) != j4o.lang.JavaSystem.GetClassForObject
				(this))
			{
				return false;
			}
			return _msgID == ((com.db4o.cs.messages.Msg)obj)._msgID;
		}

		internal virtual void FakePayLoad(com.db4o.Transaction a_trans)
		{
			_trans = a_trans;
		}

		/// <summary>
		/// dummy method to allow clean override handling
		/// without casting
		/// </summary>
		public virtual com.db4o.YapReader GetByteLoad()
		{
			return null;
		}

		internal string GetName()
		{
			if (_name == null)
			{
				return j4o.lang.JavaSystem.GetClassForObject(this).GetName();
			}
			return _name;
		}

		internal virtual com.db4o.Transaction GetTransaction()
		{
			return _trans;
		}

		internal virtual com.db4o.YapStream GetStream()
		{
			return GetTransaction().Stream();
		}

		protected virtual object StreamLock()
		{
			return GetStream().Lock();
		}

		/// <summary>server side execution</summary>
		public virtual bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket 
			socket)
		{
			return false;
		}

		public static com.db4o.cs.messages.Msg ReadMessage(com.db4o.Transaction a_trans, 
			com.db4o.foundation.network.YapSocket sock)
		{
			com.db4o.YapWriter reader = new com.db4o.YapWriter(a_trans, com.db4o.YapConst.MESSAGE_LENGTH
				);
			if (!reader.Read(sock))
			{
				return null;
			}
			com.db4o.cs.messages.Msg message = _messages[reader.ReadInt()].ReadPayLoad(a_trans
				, sock, reader);
			return message;
		}

		internal virtual com.db4o.cs.messages.Msg ReadPayLoad(com.db4o.Transaction a_trans
			, com.db4o.foundation.network.YapSocket sock, com.db4o.YapReader reader)
		{
			a_trans = CheckParentTransaction(a_trans, reader);
			return Clone(a_trans);
		}

		protected virtual com.db4o.Transaction CheckParentTransaction(com.db4o.Transaction
			 a_trans, com.db4o.YapReader reader)
		{
			if (reader.ReadByte() == com.db4o.YapConst.SYSTEM_TRANS && a_trans.ParentTransaction
				() != null)
			{
				return a_trans.ParentTransaction();
			}
			return a_trans;
		}

		internal void SetTransaction(com.db4o.Transaction aTrans)
		{
			_trans = aTrans;
		}

		public sealed override string ToString()
		{
			return GetName();
		}

		public void Write(com.db4o.YapStream stream, com.db4o.foundation.network.YapSocket
			 sock)
		{
			lock (sock)
			{
				try
				{
					sock.Write(PayLoad()._buffer);
					sock.Flush();
				}
				catch
				{
				}
			}
		}

		public virtual com.db4o.YapWriter PayLoad()
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(GetTransaction(), com.db4o.YapConst
				.MESSAGE_LENGTH);
			writer.WriteInt(_msgID);
			return writer;
		}

		internal void WriteQueryResult(com.db4o.inside.query.QueryResult qr, com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.Transaction trans = GetTransaction();
			int size = qr.Size();
			com.db4o.cs.messages.MsgD message = ID_LIST.GetWriterForLength(trans, com.db4o.YapConst
				.ID_LENGTH * (size + 1));
			com.db4o.YapWriter writer = message.PayLoad();
			writer.WriteQueryResult(qr);
			message.Write(GetStream(), sock);
		}
	}
}
