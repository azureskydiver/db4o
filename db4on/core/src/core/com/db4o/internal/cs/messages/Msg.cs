namespace com.db4o.@internal.cs.messages
{
	/// <summary>Messages for Client/Server Communication</summary>
	public class Msg : j4o.lang.Cloneable
	{
		internal static int _idGenerator = 1;

		private static com.db4o.@internal.cs.messages.Msg[] _messages = new com.db4o.@internal.cs.messages.Msg
			[60];

		internal int _msgID;

		internal string _name;

		internal com.db4o.@internal.Transaction _trans;

		public static readonly com.db4o.@internal.cs.messages.MsgD CLASS_NAME_FOR_ID = new 
			com.db4o.@internal.cs.messages.MClassNameForID();

		public static readonly com.db4o.@internal.cs.messages.Msg CLOSE = new com.db4o.@internal.cs.messages.Msg
			("CLOSE");

		public static readonly com.db4o.@internal.cs.messages.Msg COMMIT = new com.db4o.@internal.cs.messages.MCommit
			();

		public static readonly com.db4o.@internal.cs.messages.Msg COMMIT_OK = new com.db4o.@internal.cs.messages.MCommitOK
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD CREATE_CLASS = new com.db4o.@internal.cs.messages.MCreateClass
			();

		public static readonly com.db4o.@internal.cs.messages.MsgObject CLASS_META = new 
			com.db4o.@internal.cs.messages.MClassMeta();

		public static readonly com.db4o.@internal.cs.messages.Msg CURRENT_VERSION = new com.db4o.@internal.cs.messages.Msg
			("VERSION");

		public static readonly com.db4o.@internal.cs.messages.MsgD DELETE = new com.db4o.@internal.cs.messages.MDelete
			();

		public static readonly com.db4o.@internal.cs.messages.Msg ERROR = new com.db4o.@internal.cs.messages.Msg
			("ERROR");

		public static readonly com.db4o.@internal.cs.messages.Msg FAILED = new com.db4o.@internal.cs.messages.Msg
			("FAILED");

		public static readonly com.db4o.@internal.cs.messages.MsgD GET_ALL = new com.db4o.@internal.cs.messages.MGetAll
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD GET_CLASSES = new com.db4o.@internal.cs.messages.MGetClasses
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD GET_INTERNAL_IDS = new 
			com.db4o.@internal.cs.messages.MGetInternalIDs();

		public static readonly com.db4o.@internal.cs.messages.Msg GET_THREAD_ID = new com.db4o.@internal.cs.messages.Msg
			("GET_THREAD_ID");

		public static readonly com.db4o.@internal.cs.messages.MsgD ID_LIST = new com.db4o.@internal.cs.messages.MsgD
			("ID_LIST");

		public static readonly com.db4o.@internal.cs.messages.Msg IDENTITY = new com.db4o.@internal.cs.messages.Msg
			("IDENTITY");

		public static readonly com.db4o.@internal.cs.messages.MsgD LENGTH = new com.db4o.@internal.cs.messages.MsgD
			("LENGTH");

		public static readonly com.db4o.@internal.cs.messages.MsgD LOGIN = new com.db4o.@internal.cs.messages.MsgD
			("LOGIN");

		public static readonly com.db4o.@internal.cs.messages.MsgD LOGIN_OK = new com.db4o.@internal.cs.messages.MsgD
			("LOGIN_OK");

		public static readonly com.db4o.@internal.cs.messages.Msg NULL = new com.db4o.@internal.cs.messages.Msg
			("NULL");

		public static readonly com.db4o.@internal.cs.messages.MsgD OBJECT_BY_UUID = new com.db4o.@internal.cs.messages.MObjectByUuid
			();

		public static readonly com.db4o.@internal.cs.messages.MsgObject OBJECT_TO_CLIENT = 
			new com.db4o.@internal.cs.messages.MsgObject();

		public static readonly com.db4o.@internal.cs.messages.MsgD OBJECTSET_FETCH = new 
			com.db4o.@internal.cs.messages.MObjectSetFetch();

		public static readonly com.db4o.@internal.cs.messages.MsgD OBJECTSET_FINALIZED = 
			new com.db4o.@internal.cs.messages.MsgD("OBJECTSET_FINALIZED");

		public static readonly com.db4o.@internal.cs.messages.MsgD OBJECTSET_GET_ID = new 
			com.db4o.@internal.cs.messages.MObjectSetGetId();

		public static readonly com.db4o.@internal.cs.messages.MsgD OBJECTSET_INDEXOF = new 
			com.db4o.@internal.cs.messages.MObjectSetIndexOf();

		public static readonly com.db4o.@internal.cs.messages.MsgD OBJECTSET_RESET = new 
			com.db4o.@internal.cs.messages.MObjectSetReset();

		public static readonly com.db4o.@internal.cs.messages.MsgD OBJECTSET_SIZE = new com.db4o.@internal.cs.messages.MObjectSetSize
			();

		public static readonly com.db4o.@internal.cs.messages.Msg OK = new com.db4o.@internal.cs.messages.Msg
			("OK");

		public static readonly com.db4o.@internal.cs.messages.Msg PING = new com.db4o.@internal.cs.messages.Msg
			("PING");

		public static readonly com.db4o.@internal.cs.messages.MsgD PREFETCH_IDS = new com.db4o.@internal.cs.messages.MPrefetchIDs
			();

		public static readonly com.db4o.@internal.cs.messages.Msg PROCESS_DELETES = new com.db4o.@internal.cs.messages.MProcessDeletes
			();

		public static readonly com.db4o.@internal.cs.messages.MsgObject QUERY_EXECUTE = new 
			com.db4o.@internal.cs.messages.MQueryExecute();

		public static readonly com.db4o.@internal.cs.messages.MsgD QUERY_RESULT = new com.db4o.@internal.cs.messages.MsgD
			("QUERY_RESULT");

		public static readonly com.db4o.@internal.cs.messages.MsgD RAISE_VERSION = new com.db4o.@internal.cs.messages.MsgD
			("RAISE_VERSION");

		public static readonly com.db4o.@internal.cs.messages.MsgBlob READ_BLOB = new com.db4o.@internal.cs.messages.MReadBlob
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD READ_BYTES = new com.db4o.@internal.cs.messages.MReadBytes
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD READ_MULTIPLE_OBJECTS = 
			new com.db4o.@internal.cs.messages.MReadMultipleObjects();

		public static readonly com.db4o.@internal.cs.messages.MsgD READ_OBJECT = new com.db4o.@internal.cs.messages.MReadObject
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD RELEASE_SEMAPHORE = new 
			com.db4o.@internal.cs.messages.MReleaseSemaphore();

		public static readonly com.db4o.@internal.cs.messages.Msg ROLLBACK = new com.db4o.@internal.cs.messages.MRollback
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD SET_SEMAPHORE = new com.db4o.@internal.cs.messages.MSetSemaphore
			();

		public static readonly com.db4o.@internal.cs.messages.Msg SUCCESS = new com.db4o.@internal.cs.messages.Msg
			("SUCCESS");

		public static readonly com.db4o.@internal.cs.messages.MsgD SWITCH_TO_FILE = new com.db4o.@internal.cs.messages.MsgD
			("SWITCH_F");

		public static readonly com.db4o.@internal.cs.messages.Msg SWITCH_TO_MAIN_FILE = new 
			com.db4o.@internal.cs.messages.Msg("SWITCH_M");

		public static readonly com.db4o.@internal.cs.messages.MsgD TA_DELETE = new com.db4o.@internal.cs.messages.MTaDelete
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD TA_IS_DELETED = new com.db4o.@internal.cs.messages.MTaIsDeleted
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD USER_MESSAGE = new com.db4o.@internal.cs.messages.MUserMessage
			();

		public static readonly com.db4o.@internal.cs.messages.MsgD USE_TRANSACTION = new 
			com.db4o.@internal.cs.messages.MUseTransaction();

		public static readonly com.db4o.@internal.cs.messages.MsgBlob WRITE_BLOB = new com.db4o.@internal.cs.messages.MWriteBlob
			();

		public static readonly com.db4o.@internal.cs.messages.MWriteNew WRITE_NEW = new com.db4o.@internal.cs.messages.MWriteNew
			();

		public static readonly com.db4o.@internal.cs.messages.MsgObject WRITE_UPDATE = new 
			com.db4o.@internal.cs.messages.MWriteUpdate();

		public static readonly com.db4o.@internal.cs.messages.MsgD WRITE_UPDATE_DELETE_MEMBERS
			 = new com.db4o.@internal.cs.messages.MWriteUpdateDeleteMembers();

		public static readonly com.db4o.@internal.cs.messages.MWriteBatchedMessages WRITE_BATCHED_MESSAGES
			 = new com.db4o.@internal.cs.messages.MWriteBatchedMessages();

		internal Msg()
		{
			_msgID = _idGenerator++;
			_messages[_msgID] = this;
		}

		internal Msg(string aName) : this()
		{
			_name = aName;
		}

		public static com.db4o.@internal.cs.messages.Msg GetMessage(int id)
		{
			return _messages[id];
		}

		public com.db4o.@internal.cs.messages.Msg Clone(com.db4o.@internal.Transaction a_trans
			)
		{
			com.db4o.@internal.cs.messages.Msg msg = null;
			try
			{
				msg = (com.db4o.@internal.cs.messages.Msg)MemberwiseClone();
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
			return _msgID == ((com.db4o.@internal.cs.messages.Msg)obj)._msgID;
		}

		public override int GetHashCode()
		{
			return _msgID;
		}

		internal virtual void FakePayLoad(com.db4o.@internal.Transaction a_trans)
		{
			_trans = a_trans;
		}

		/// <summary>
		/// dummy method to allow clean override handling
		/// without casting
		/// </summary>
		public virtual com.db4o.@internal.Buffer GetByteLoad()
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

		protected virtual com.db4o.@internal.Transaction Transaction()
		{
			return _trans;
		}

		protected virtual com.db4o.@internal.LocalObjectContainer File()
		{
			return (com.db4o.@internal.LocalObjectContainer)Stream();
		}

		protected virtual com.db4o.@internal.ObjectContainerBase Stream()
		{
			return Transaction().Stream();
		}

		protected virtual object StreamLock()
		{
			return Stream().Lock();
		}

		protected virtual com.db4o.@internal.Config4Impl Config()
		{
			return Stream().Config();
		}

		/// <summary>server side execution</summary>
		/// <param name="serverThread">TODO</param>
		public virtual bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			return false;
		}

		public static com.db4o.@internal.cs.messages.Msg ReadMessage(com.db4o.@internal.Transaction
			 a_trans, com.db4o.foundation.network.Socket4 sock)
		{
			com.db4o.@internal.StatefulBuffer reader = new com.db4o.@internal.StatefulBuffer(
				a_trans, com.db4o.@internal.Const4.MESSAGE_LENGTH);
			if (!reader.Read(sock))
			{
				return null;
			}
			com.db4o.@internal.cs.messages.Msg message = _messages[reader.ReadInt()].ReadPayLoad
				(a_trans, sock, reader);
			return message;
		}

		internal virtual com.db4o.@internal.cs.messages.Msg ReadPayLoad(com.db4o.@internal.Transaction
			 a_trans, com.db4o.foundation.network.Socket4 sock, com.db4o.@internal.Buffer reader
			)
		{
			a_trans = CheckParentTransaction(a_trans, reader);
			return Clone(a_trans);
		}

		protected virtual com.db4o.@internal.Transaction CheckParentTransaction(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.Buffer reader)
		{
			if (reader.ReadByte() == com.db4o.@internal.Const4.SYSTEM_TRANS && a_trans.ParentTransaction
				() != null)
			{
				return a_trans.ParentTransaction();
			}
			return a_trans;
		}

		internal void SetTransaction(com.db4o.@internal.Transaction aTrans)
		{
			_trans = aTrans;
		}

		public sealed override string ToString()
		{
			return GetName();
		}

		public void Write(com.db4o.@internal.ObjectContainerBase stream, com.db4o.foundation.network.Socket4
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

		public virtual com.db4o.@internal.StatefulBuffer PayLoad()
		{
			com.db4o.@internal.StatefulBuffer writer = new com.db4o.@internal.StatefulBuffer(
				Transaction(), com.db4o.@internal.Const4.MESSAGE_LENGTH);
			writer.WriteInt(_msgID);
			return writer;
		}
	}
}
