namespace com.db4o
{
	/// <summary>Messages for Client/Server Communication</summary>
	internal class Msg : j4o.lang.Cloneable
	{
		internal static int idGenererator = 1;

		internal int i_msgID;

		internal string i_name;

		internal com.db4o.Transaction i_trans;

		private static com.db4o.Msg[] i_messages = new com.db4o.Msg[60];

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

		public static readonly com.db4o.Msg NULL = new com.db4o.Msg("NULL");

		public static readonly com.db4o.MsgD OBJECT_BY_UUID = new com.db4o.MObjectByUuid(
			);

		public static readonly com.db4o.MsgObject OBJECT_TO_CLIENT = new com.db4o.MsgObject
			();

		public static readonly com.db4o.Msg OK = new com.db4o.Msg("OK");

		public static readonly com.db4o.Msg PING = new com.db4o.Msg("PING");

		public static readonly com.db4o.Msg PREFETCH_IDS = new com.db4o.MPrefetchIDs();

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
			i_msgID = idGenererator++;
			i_messages[i_msgID] = this;
		}

		internal Msg(string aName) : this()
		{
			i_name = aName;
		}

		internal com.db4o.Msg clone(com.db4o.Transaction a_trans)
		{
			try
			{
				com.db4o.Msg msg = (com.db4o.Msg)j4o.lang.JavaSystem.clone(this);
				msg.i_trans = a_trans;
				return msg;
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
				return null;
			}
		}

		public sealed override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null || j4o.lang.Class.getClassForObject(obj) != j4o.lang.Class.getClassForObject
				(this))
			{
				return false;
			}
			return i_msgID == ((com.db4o.Msg)obj).i_msgID;
		}

		internal virtual void fakePayLoad(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
		}

		/// <summary>
		/// dummy method to allow clean override handling
		/// without casting
		/// </summary>
		internal virtual com.db4o.YapWriter getByteLoad()
		{
			return null;
		}

		internal string getName()
		{
			if (i_name == null)
			{
				return j4o.lang.Class.getClassForObject(this).getName();
			}
			return i_name;
		}

		internal virtual com.db4o.Transaction getTransaction()
		{
			return i_trans;
		}

		internal virtual com.db4o.YapStream getStream()
		{
			return getTransaction().i_stream;
		}

		/// <summary>server side execution</summary>
		internal virtual bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 socket)
		{
			return false;
		}

		internal static com.db4o.Msg readMessage(com.db4o.Transaction a_trans, com.db4o.foundation.network.YapSocket
			 sock)
		{
			com.db4o.YapWriter reader = new com.db4o.YapWriter(a_trans, com.db4o.YapConst.MESSAGE_LENGTH
				);
			try
			{
				if (!reader.read(sock))
				{
					return null;
				}
				com.db4o.Msg message = i_messages[reader.readInt()].readPayLoad(a_trans, sock, reader
					);
				return message;
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		internal virtual com.db4o.Msg readPayLoad(com.db4o.Transaction a_trans, com.db4o.foundation.network.YapSocket
			 sock, com.db4o.YapWriter reader)
		{
			if (reader.readByte() == com.db4o.YapConst.SYSTEM_TRANS && a_trans.i_parentTransaction
				 != null)
			{
				a_trans = a_trans.i_parentTransaction;
			}
			return clone(a_trans);
		}

		internal void setTransaction(com.db4o.Transaction aTrans)
		{
			i_trans = aTrans;
		}

		public sealed override string ToString()
		{
			return getName();
		}

		internal void write(com.db4o.YapStream stream, com.db4o.foundation.network.YapSocket
			 sock)
		{
			lock (sock)
			{
				try
				{
					sock.write(getPayLoad()._buffer);
					sock.flush();
				}
				catch (System.Exception e)
				{
				}
			}
		}

		internal virtual com.db4o.YapWriter getPayLoad()
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(getTransaction(), com.db4o.YapConst
				.MESSAGE_LENGTH);
			writer.writeInt(i_msgID);
			return writer;
		}

		internal void writeQueryResult(com.db4o.Transaction a_trans, com.db4o.QResult qr, 
			com.db4o.foundation.network.YapSocket sock)
		{
			int size = qr.size();
			com.db4o.MsgD message = ID_LIST.getWriterForLength(a_trans, com.db4o.YapConst.YAPID_LENGTH
				 * (size + 1));
			com.db4o.YapWriter writer = message.getPayLoad();
			writer.writeQueryResult(qr);
			message.write(a_trans.i_stream, sock);
		}
	}
}
