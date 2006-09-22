namespace com.db4o
{
	/// <summary>Messages for Client/Server Communication</summary>
	internal class Msg : j4o.lang.Cloneable
	{
		internal static int _idGenerator = 1;

		private static com.db4o.Msg[] _messages = new com.db4o.Msg[60];

		internal int _msgID;

		internal string _name;

		internal com.db4o.Transaction _trans;

		public static readonly com.db4o.MsgD CLASS_NAME_FOR_ID = new com.db4o.MClassNameForID
			();

		public static readonly com.db4o.Msg CLOSE = new com.db4o.Msg("CLOSE");

		public static readonly com.db4o.Msg COMMIT = new com.db4o.MCommit();

		public static readonly com.db4o.Msg COMMIT_OK = new com.db4o.MCommitOK();

		public static readonly com.db4o.MsgD CREATE_CLASS = new com.db4o.MCreateClass();

		public static readonly com.db4o.Msg CURRENT_VERSION = new com.db4o.Msg("VERSION");

		public static readonly com.db4o.MsgD DELETE = new com.db4o.MDelete();

		public static readonly com.db4o.Msg ERROR = new com.db4o.Msg("ERROR");

		public static readonly com.db4o.Msg FAILED = new com.db4o.Msg("FAILED");

		public static readonly com.db4o.Msg GET_ALL = new com.db4o.MGetAll();

		public static readonly com.db4o.MsgD GET_CLASSES = new com.db4o.MGetClasses();

		public static readonly com.db4o.MsgD GET_INTERNAL_IDS = new com.db4o.MGetInternalIDs
			();

		public static readonly com.db4o.Msg GET_THREAD_ID = new com.db4o.Msg("GET_THREAD_ID"
			);

		public static readonly com.db4o.MsgD ID_LIST = new com.db4o.MsgD("ID_LIST");

		public static readonly com.db4o.Msg IDENTITY = new com.db4o.Msg("IDENTITY");

		public static readonly com.db4o.MsgD LENGTH = new com.db4o.MsgD("LENGTH");

		public static readonly com.db4o.MsgD LOGIN = new com.db4o.MsgD("LOGIN");

		public static readonly com.db4o.MsgD LOGIN_OK = new com.db4o.MsgD("LOGIN_OK");

		public static readonly com.db4o.Msg NULL = new com.db4o.Msg("NULL");

		public static readonly com.db4o.MsgD OBJECT_BY_UUID = new com.db4o.MObjectByUuid(
			);

		public static readonly com.db4o.MsgObject OBJECT_TO_CLIENT = new com.db4o.MsgObject
			();

		public static readonly com.db4o.Msg OK = new com.db4o.Msg("OK");

		public static readonly com.db4o.Msg PING = new com.db4o.Msg("PING");

		public static readonly com.db4o.MsgD PREFETCH_IDS = new com.db4o.MPrefetchIDs();

		public static readonly com.db4o.MsgObject QUERY_EXECUTE = new com.db4o.MQueryExecute
			();

		public static readonly com.db4o.MsgD RAISE_VERSION = new com.db4o.MsgD("RAISE_VERSION"
			);

		public static readonly com.db4o.MsgBlob READ_BLOB = new com.db4o.MReadBlob();

		public static readonly com.db4o.MsgD READ_BYTES = new com.db4o.MReadBytes();

		public static readonly com.db4o.MsgD READ_MULTIPLE_OBJECTS = new com.db4o.MReadMultipleObjects
			();

		public static readonly com.db4o.MsgD READ_OBJECT = new com.db4o.MReadObject();

		public static readonly com.db4o.MsgD RELEASE_SEMAPHORE = new com.db4o.MReleaseSemaphore
			();

		public static readonly com.db4o.Msg ROLLBACK = new com.db4o.MRollback();

		public static readonly com.db4o.MsgD SET_SEMAPHORE = new com.db4o.MSetSemaphore();

		public static readonly com.db4o.Msg SUCCESS = new com.db4o.Msg("SUCCESS");

		public static readonly com.db4o.MsgD SWITCH_TO_FILE = new com.db4o.MsgD("SWITCH_F"
			);

		public static readonly com.db4o.Msg SWITCH_TO_MAIN_FILE = new com.db4o.Msg("SWITCH_M"
			);

		public static readonly com.db4o.Msg TA_BEGIN_END_SET = new com.db4o.MTaBeginEndSet
			();

		public static readonly com.db4o.MsgD TA_DELETE = new com.db4o.MTaDelete();

		public static readonly com.db4o.MsgD TA_DONT_DELETE = new com.db4o.MTaDontDelete(
			);

		public static readonly com.db4o.MsgD TA_IS_DELETED = new com.db4o.MTaIsDeleted();

		public static readonly com.db4o.MsgD USER_MESSAGE = new com.db4o.MUserMessage();

		public static readonly com.db4o.MsgD USE_TRANSACTION = new com.db4o.MUseTransaction
			();

		public static readonly com.db4o.MsgBlob WRITE_BLOB = new com.db4o.MWriteBlob();

		public static readonly com.db4o.MWriteNew WRITE_NEW = new com.db4o.MWriteNew();

		public static readonly com.db4o.MsgObject WRITE_UPDATE = new com.db4o.MWriteUpdate
			();

		public static readonly com.db4o.MsgD WRITE_UPDATE_DELETE_MEMBERS = new com.db4o.MWriteUpdateDeleteMembers
			();

		internal Msg()
		{
			_msgID = _idGenerator++;
			_messages[_msgID] = this;
		}

		internal Msg(string aName) : this()
		{
			_name = aName;
		}

		internal com.db4o.Msg Clone(com.db4o.Transaction a_trans)
		{
			com.db4o.Msg msg = null;
			try
			{
				msg = (com.db4o.Msg)MemberwiseClone();
				msg._trans = a_trans;
			}
			catch (j4o.lang.CloneNotSupportedException e)
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
			if (obj == null || j4o.lang.Class.GetClassForObject(obj) != j4o.lang.Class.GetClassForObject
				(this))
			{
				return false;
			}
			return _msgID == ((com.db4o.Msg)obj)._msgID;
		}

		internal virtual void FakePayLoad(com.db4o.Transaction a_trans)
		{
			_trans = a_trans;
		}

		/// <summary>
		/// dummy method to allow clean override handling
		/// without casting
		/// </summary>
		internal virtual com.db4o.YapWriter GetByteLoad()
		{
			return null;
		}

		internal string GetName()
		{
			if (_name == null)
			{
				return j4o.lang.Class.GetClassForObject(this).GetName();
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

		/// <summary>server side execution</summary>
		internal virtual bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 socket)
		{
			return false;
		}

		internal static com.db4o.Msg ReadMessage(com.db4o.Transaction a_trans, com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapWriter reader = new com.db4o.YapWriter(a_trans, com.db4o.YapConst.MESSAGE_LENGTH
				);
			if (!reader.Read(sock))
			{
				return null;
			}
			com.db4o.Msg message = _messages[reader.ReadInt()].ReadPayLoad(a_trans, sock, reader
				);
			return message;
		}

		internal virtual com.db4o.Msg ReadPayLoad(com.db4o.Transaction a_trans, com.db4o.foundation.network.YapSocket
			 sock, com.db4o.YapWriter reader)
		{
			if (reader.ReadByte() == com.db4o.YapConst.SYSTEM_TRANS && a_trans.i_parentTransaction
				 != null)
			{
				a_trans = a_trans.i_parentTransaction;
			}
			return Clone(a_trans);
		}

		internal void SetTransaction(com.db4o.Transaction aTrans)
		{
			_trans = aTrans;
		}

		public sealed override string ToString()
		{
			return GetName();
		}

		internal void Write(com.db4o.YapStream stream, com.db4o.foundation.network.YapSocket
			 sock)
		{
			lock (sock)
			{
				try
				{
					sock.Write(GetPayLoad()._buffer);
					sock.Flush();
				}
				catch (System.Exception e)
				{
				}
			}
		}

		internal virtual com.db4o.YapWriter GetPayLoad()
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(GetTransaction(), com.db4o.YapConst
				.MESSAGE_LENGTH);
			writer.WriteInt(_msgID);
			return writer;
		}

		internal void WriteQueryResult(com.db4o.Transaction a_trans, com.db4o.QueryResultImpl
			 qr, com.db4o.foundation.network.YapSocket sock)
		{
			int size = qr.Size();
			com.db4o.MsgD message = ID_LIST.GetWriterForLength(a_trans, com.db4o.YapConst.ID_LENGTH
				 * (size + 1));
			com.db4o.YapWriter writer = message.GetPayLoad();
			writer.WriteQueryResult(qr);
			message.Write(a_trans.Stream(), sock);
		}
	}
}
