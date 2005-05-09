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
	/// <exclude></exclude>
	public class YapClient : com.db4o.YapStream, com.db4o.ext.ExtClient
	{
		internal readonly object blobLock = new object();

		private com.db4o.YapClientBlobThread blobThread;

		private com.db4o.YapSocket i_socket;

		internal com.db4o.Queue4 messageQueue = new com.db4o.Queue4();

		internal readonly com.db4o.Lock4 messageQueueLock = new com.db4o.Lock4();

		private string password;

		internal int[] prefetchedIDs = new int[com.db4o.YapConst.PREFETCH_ID_COUNT];

		internal com.db4o.YapClientThread readerThread;

		internal int remainingIDs;

		private string switchedToFile;

		private bool singleThreaded;

		private string userName;

		private com.db4o.ext.Db4oDatabase i_db;

		private YapClient() : base(null)
		{
		}

		public YapClient(string fakeServerFile) : this()
		{
			lock (Lock())
			{
				singleThreaded = i_config.i_singleThreadedClient;
				throw new j4o.lang.RuntimeException("This constructor is for Debug.fakeServer use only."
					);
				initialize3();
				com.db4o.Platform.postOpen(this);
			}
		}

		internal YapClient(com.db4o.YapSocket socket, string user, string password, bool 
			login) : this()
		{
			lock (Lock())
			{
				singleThreaded = i_config.i_singleThreadedClient;
				if (password == null)
				{
					throw new System.ArgumentNullException(com.db4o.Messages.get(56));
				}
				if (!login)
				{
					password = null;
				}
				userName = user;
				this.password = password;
				i_socket = socket;
				try
				{
					loginToServer(socket);
				}
				catch (j4o.io.IOException e)
				{
					i_references.stopTimer();
					throw e;
				}
				if (!singleThreaded)
				{
					readerThread = new com.db4o.YapClientThread(this, socket, messageQueue, messageQueueLock
						);
					readerThread.setName("db4o message client for user " + user);
					readerThread.start();
				}
				logMsg(36, ToString());
				readThis();
				initialize3();
				com.db4o.Platform.postOpen(this);
			}
		}

		public override void backup(string path)
		{
			com.db4o.Db4o.throwRuntimeException(60);
		}

		internal override bool close2()
		{
			try
			{
				com.db4o.Msg.CLOSE.write(this, i_socket);
			}
			catch (System.Exception e)
			{
			}
			try
			{
				if (!singleThreaded)
				{
					readerThread.close();
				}
			}
			catch (System.Exception e)
			{
			}
			try
			{
				i_socket.close();
			}
			catch (System.Exception e)
			{
			}
			bool ret = base.close2();
			return ret;
		}

		internal sealed override void commit1()
		{
			i_trans.commit();
		}

		internal sealed override com.db4o.ClassIndex createClassIndex(com.db4o.YapClass a_yapClass
			)
		{
			return new com.db4o.ClassIndexClient(a_yapClass);
		}

		internal virtual com.db4o.YapSocket createParalellSocket()
		{
			com.db4o.Msg.GET_THREAD_ID.write(this, i_socket);
			int serverThreadID = expectedByteResponse(com.db4o.Msg.ID_LIST).readInt();
			com.db4o.YapSocket sock = null;
			if (i_socket is com.db4o.YapSocketFake)
			{
				sock = ((com.db4o.YapSocketFake)i_socket).i_server.openFakeClientSocket();
			}
			else
			{
				sock = new com.db4o.YapSocket(i_socket.getHostName(), i_socket.getPort());
				loginToServer(sock);
			}
			if (switchedToFile != null)
			{
				com.db4o.MsgD message = com.db4o.Msg.SWITCH_TO_FILE.getWriterForString(i_systemTrans
					, switchedToFile);
				message.write(this, sock);
				if (!(com.db4o.Msg.OK.Equals(com.db4o.Msg.readMessage(i_systemTrans, sock))))
				{
					throw new j4o.io.IOException(com.db4o.Messages.get(42));
				}
			}
			com.db4o.Msg.USE_TRANSACTION.getWriterForInt(i_trans, serverThreadID).write(this, 
				sock);
			return sock;
		}

		internal sealed override com.db4o.QResult createQResult(com.db4o.Transaction a_ta
			)
		{
			return new com.db4o.QResultClient(a_ta);
		}

		internal sealed override void createTransaction()
		{
			i_systemTrans = new com.db4o.TransactionClient(this, null);
			i_trans = new com.db4o.TransactionClient(this, i_systemTrans);
		}

		internal override bool createYapClass(com.db4o.YapClass a_yapClass, com.db4o.reflect.ReflectClass
			 a_class, com.db4o.YapClass a_superYapClass)
		{
			writeMsg(com.db4o.Msg.CREATE_CLASS.getWriterForString(i_systemTrans, a_class.getName
				()));
			com.db4o.MsgObject message = (com.db4o.MsgObject)expectedResponse(com.db4o.Msg.OBJECT_TO_CLIENT
				);
			com.db4o.YapWriter bytes = message.unmarshall();
			if (bytes == null)
			{
				return false;
			}
			bytes.setTransaction(getSystemTransaction());
			if (!base.createYapClass(a_yapClass, a_class, a_superYapClass))
			{
				return false;
			}
			a_yapClass.setID(this, message.i_id);
			a_yapClass.readName1(getSystemTransaction(), bytes);
			i_classCollection.addYapClass(a_yapClass);
			i_classCollection.readYapClass(a_yapClass, a_class);
			return true;
		}

		internal override long currentVersion()
		{
			writeMsg(com.db4o.Msg.CURRENT_VERSION);
			return ((com.db4o.MsgD)expectedResponse(com.db4o.Msg.ID_LIST)).readLong();
		}

		internal sealed override bool delete5(com.db4o.Transaction ta, com.db4o.YapObject
			 yo, int a_cascade, bool userCall)
		{
			writeMsg(com.db4o.Msg.DELETE.getWriterForInts(i_trans, new int[] { yo.getID(), userCall
				 ? 1 : 0 }));
			return true;
		}

		internal override bool detectSchemaChanges()
		{
			return false;
		}

		internal com.db4o.YapWriter expectedByteResponse(com.db4o.Msg expectedMessage)
		{
			com.db4o.Msg msg = expectedResponse(expectedMessage);
			if (msg == null)
			{
				return null;
			}
			return msg.getByteLoad();
		}

		internal com.db4o.Msg expectedResponse(com.db4o.Msg expectedMessage)
		{
			com.db4o.Msg message = getResponse();
			if (expectedMessage.Equals(message))
			{
				return message;
			}
			return null;
		}

		internal void free(int a_address, int a_length)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal override void getAll(com.db4o.Transaction ta, com.db4o.QResult a_res)
		{
			writeMsg(com.db4o.Msg.GET_ALL);
			readResult(a_res);
		}

		/// <summary>may return null, if no message is returned.</summary>
		/// <remarks>
		/// may return null, if no message is returned. Error handling is weak and
		/// should ideally be able to trigger some sort of state listener
		/// (connection dead) on the client.
		/// </remarks>
		internal virtual com.db4o.Msg getResponse()
		{
			if (singleThreaded)
			{
				while (i_socket != null)
				{
					try
					{
						com.db4o.Msg message = com.db4o.Msg.readMessage(i_trans, i_socket);
						if (com.db4o.Msg.PING.Equals(message))
						{
							writeMsg(com.db4o.Msg.OK);
						}
						else
						{
							if (com.db4o.Msg.CLOSE.Equals(message))
							{
								logMsg(35, ToString());
								close();
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
			else
			{
				return (com.db4o.Msg)messageQueueLock.run(new _AnonymousInnerClass263(this));
			}
		}

		private sealed class _AnonymousInnerClass263 : com.db4o.Closure4
		{
			public _AnonymousInnerClass263(YapClient _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object run()
			{
				com.db4o.Msg message = null;
				message = (com.db4o.Msg)this._enclosing.messageQueue.next();
				if (message != null)
				{
					return message;
				}
				if (this._enclosing.readerThread.isClosed())
				{
					com.db4o.Db4o.throwRuntimeException(20, this._enclosing.name());
				}
				this._enclosing.messageQueueLock.snooze(this._enclosing.i_config.i_timeoutClientSocket
					);
				if (this._enclosing.readerThread.isClosed())
				{
					com.db4o.Db4o.throwRuntimeException(20, this._enclosing.name());
				}
				message = (com.db4o.Msg)this._enclosing.messageQueue.next();
				return message;
			}

			private readonly YapClient _enclosing;
		}

		internal override com.db4o.YapClass getYapClass(int a_id)
		{
			com.db4o.YapClass yc = base.getYapClass(a_id);
			if (yc != null)
			{
				return yc;
			}
			writeMsg(com.db4o.Msg.CLASS_NAME_FOR_ID.getWriterForInt(i_systemTrans, a_id));
			com.db4o.MsgD message = (com.db4o.MsgD)expectedResponse(com.db4o.Msg.CLASS_NAME_FOR_ID
				);
			string className = message.readString();
			if (className != null && j4o.lang.JavaSystem.getLengthOf(className) > 0)
			{
				com.db4o.reflect.ReflectClass claxx = reflector().forName(className);
				if (claxx != null)
				{
					return getYapClass(claxx, true);
				}
			}
			return null;
		}

		internal com.db4o.YapSocket agetYapSocket()
		{
			return i_socket;
		}

		internal override bool needsLockFileThread()
		{
			return false;
		}

		internal override bool hasShutDownHook()
		{
			return false;
		}

		public override com.db4o.ext.Db4oDatabase identity()
		{
			if (i_db == null)
			{
				writeMsg(com.db4o.Msg.IDENTITY);
				com.db4o.YapWriter reader = expectedByteResponse(com.db4o.Msg.ID_LIST);
				showInternalClasses(true);
				i_db = (com.db4o.ext.Db4oDatabase)getByID(reader.readInt());
				activate1(i_systemTrans, i_db, 3);
				showInternalClasses(false);
			}
			return i_db;
		}

		internal override bool isClient()
		{
			return true;
		}

		internal virtual void loginToServer(com.db4o.YapSocket a_socket)
		{
			if (password != null)
			{
				com.db4o.YapStringIOUnicode stringWriter = new com.db4o.YapStringIOUnicode();
				int length = stringWriter.length(userName) + stringWriter.length(password);
				com.db4o.MsgD message = com.db4o.Msg.LOGIN.getWriterForLength(i_systemTrans, length
					);
				message.writeString(userName);
				message.writeString(password);
				message.write(this, a_socket);
				if (!com.db4o.Msg.OK.Equals(com.db4o.Msg.readMessage(i_systemTrans, a_socket)))
				{
					throw new j4o.io.IOException(com.db4o.Messages.get(42));
				}
			}
		}

		internal override bool maintainsIndices()
		{
			return false;
		}

		internal sealed override com.db4o.YapWriter newObject(com.db4o.Transaction a_trans
			, com.db4o.YapMeta a_object)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal sealed override int newUserObject()
		{
			com.db4o.YapWriter reader = null;
			if (remainingIDs < 1)
			{
				writeMsg(com.db4o.Msg.PREFETCH_IDS);
				reader = expectedByteResponse(com.db4o.Msg.ID_LIST);
				for (int i = com.db4o.YapConst.PREFETCH_ID_COUNT - 1; i >= 0; i--)
				{
					prefetchedIDs[i] = reader.readInt();
				}
				remainingIDs = com.db4o.YapConst.PREFETCH_ID_COUNT;
			}
			remainingIDs--;
			return prefetchedIDs[remainingIDs];
		}

		internal virtual int prefetchObjects(com.db4o.QResultClient qResult, object[] prefetched
			, int prefetchCount)
		{
			int count = 0;
			int toGet = 0;
			int[] idsToGet = new int[prefetchCount];
			int[] position = new int[prefetchCount];
			while (qResult.hasNext() && (count < prefetchCount))
			{
				bool foundInCache = false;
				int id = qResult.nextInt();
				if (id > 0)
				{
					com.db4o.YapObject yo = getYapObject(id);
					if (yo != null)
					{
						object candidate = yo.getObject();
						if (candidate != null)
						{
							prefetched[count] = candidate;
							foundInCache = true;
						}
						else
						{
							yapObjectGCd(yo);
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
				writeMsg(com.db4o.Msg.READ_MULTIPLE_OBJECTS.getWriterForIntArray(i_trans, idsToGet
					, toGet));
				com.db4o.MsgD message = (com.db4o.MsgD)expectedResponse(com.db4o.Msg.READ_MULTIPLE_OBJECTS
					);
				int embeddedMessageCount = message.readInt();
				for (int i = 0; i < embeddedMessageCount; i++)
				{
					com.db4o.MsgObject mso = (com.db4o.MsgObject)com.db4o.Msg.OBJECT_TO_CLIENT.clone(
						qResult.i_trans);
					mso.payLoad = message.payLoad.readYapBytes();
					if (mso.payLoad != null)
					{
						mso.payLoad.incrementOffset(com.db4o.YapConst.MESSAGE_LENGTH);
						com.db4o.YapWriter reader = mso.unmarshall(com.db4o.YapConst.MESSAGE_LENGTH);
						prefetched[position[i]] = new com.db4o.YapObject(idsToGet[i]).readPrefetch(this, 
							qResult.i_trans, reader);
					}
				}
			}
			return count;
		}

		internal virtual void processBlobMessage(com.db4o.MsgBlob msg)
		{
			lock (blobLock)
			{
				bool needStart = blobThread == null || blobThread.isTerminated();
				if (needStart)
				{
					blobThread = new com.db4o.YapClientBlobThread(this);
				}
				blobThread.add(msg);
				if (needStart)
				{
					blobThread.start();
				}
			}
		}

		internal void queryExecute(com.db4o.QQuery a_query, com.db4o.QResult a_res)
		{
			writeMsg(com.db4o.Msg.QUERY_EXECUTE.getWriter(marshall(a_query.getTransaction(), 
				a_query)));
			readResult(a_res);
			a_res.reset();
		}

		internal override void raiseVersion(long a_minimumVersion)
		{
			writeMsg(com.db4o.Msg.RAISE_VERSION.getWriterForLong(i_trans, a_minimumVersion));
		}

		internal override void readBytes(byte[] bytes, int address, int addressOffset, int
			 length)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal override void readBytes(byte[] a_bytes, int a_address, int a_length)
		{
			writeMsg(com.db4o.Msg.READ_BYTES.getWriterForInts(i_trans, new int[] { a_address, 
				a_length }));
			com.db4o.YapWriter reader = expectedByteResponse(com.db4o.Msg.READ_BYTES);
			j4o.lang.JavaSystem.arraycopy(reader._buffer, 0, a_bytes, 0, a_length);
		}

		protected override bool rename1(com.db4o.Config4Impl config)
		{
			logMsg(58, null);
			return false;
		}

		public sealed override com.db4o.YapWriter readWriterByID(com.db4o.Transaction a_ta
			, int a_id)
		{
			try
			{
				writeMsg(com.db4o.Msg.READ_OBJECT.getWriterForInt(a_ta, a_id));
				com.db4o.YapWriter bytes = ((com.db4o.MsgObject)expectedResponse(com.db4o.Msg.OBJECT_TO_CLIENT
					)).unmarshall();
				bytes.setTransaction(a_ta);
				return bytes;
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		internal sealed override com.db4o.YapReader readReaderByID(com.db4o.Transaction a_ta
			, int a_id)
		{
			return readWriterByID(a_ta, a_id);
		}

		private void readResult(com.db4o.QResult aRes)
		{
			com.db4o.YapWriter reader = expectedByteResponse(com.db4o.Msg.ID_LIST);
			int size = reader.readInt();
			for (int i = 0; i < size; i++)
			{
				aRes.add(reader.readInt());
			}
		}

		internal virtual void readThis()
		{
			writeMsg(com.db4o.Msg.GET_CLASSES.getWriter(i_systemTrans));
			com.db4o.YapWriter bytes = expectedByteResponse(com.db4o.Msg.GET_CLASSES);
			i_classCollection.setID(this, bytes.readInt());
			createStringIO(bytes.readByte());
			i_classCollection.read(i_systemTrans);
			i_classCollection.refreshClasses();
		}

		public override void releaseSemaphore(string name)
		{
			if (name == null)
			{
				throw new System.ArgumentNullException();
			}
			writeMsg(com.db4o.Msg.RELEASE_SEMAPHORE.getWriterForString(i_trans, name));
		}

		internal override void releaseSemaphores(com.db4o.Transaction ta)
		{
		}

		private void reReadAll()
		{
			remainingIDs = 0;
			initialize0();
			initialize1();
			createTransaction();
			readThis();
		}

		internal sealed override void rollback1()
		{
			writeMsg(com.db4o.Msg.ROLLBACK);
			i_trans.rollback();
		}

		public override void send(object obj)
		{
			lock (i_lock)
			{
				if (obj != null)
				{
					writeMsg(com.db4o.Msg.USER_MESSAGE.getWriter(marshall(i_trans, obj)));
				}
			}
		}

		internal sealed override void setDirty(com.db4o.UseSystemTransaction a_object)
		{
		}

		public override bool setSemaphore(string name, int timeout)
		{
			if (name == null)
			{
				throw new System.ArgumentNullException();
			}
			writeMsg(com.db4o.Msg.SET_SEMAPHORE.getWriterForIntString(i_trans, timeout, name)
				);
			com.db4o.Msg message = getResponse();
			return (message.Equals(com.db4o.Msg.SUCCESS));
		}

		public virtual void switchToFile(string fileName)
		{
			lock (i_lock)
			{
				commit();
				writeMsg(com.db4o.Msg.SWITCH_TO_FILE.getWriterForString(i_trans, fileName));
				expectedResponse(com.db4o.Msg.OK);
				reReadAll();
				switchedToFile = fileName;
			}
		}

		public virtual void switchToMainFile()
		{
			lock (i_lock)
			{
				commit();
				writeMsg(com.db4o.Msg.SWITCH_TO_MAIN_FILE);
				expectedResponse(com.db4o.Msg.OK);
				reReadAll();
				switchedToFile = null;
			}
		}

		public virtual string name()
		{
			return ToString();
		}

		public override string ToString()
		{
			return "Client Connection " + userName;
		}

		internal sealed override com.db4o.YapWriter updateObject(com.db4o.Transaction a_trans
			, com.db4o.YapMeta a_object)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal override void write(bool shuttingDown)
		{
		}

		internal sealed override void writeDirty()
		{
		}

		internal sealed override void writeEmbedded(com.db4o.YapWriter a_parent, com.db4o.YapWriter
			 a_child)
		{
			a_parent.addEmbedded(a_child);
		}

		internal void writeMsg(com.db4o.Msg a_message)
		{
			a_message.write(this, i_socket);
		}

		internal sealed override void writeNew(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 aWriter)
		{
			writeMsg(com.db4o.Msg.WRITE_NEW.getWriter(a_yapClass, aWriter));
		}

		internal sealed override void writeTransactionPointer(int a_address)
		{
		}

		internal sealed override void writeUpdate(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 a_bytes)
		{
			writeMsg(com.db4o.Msg.WRITE_UPDATE.getWriter(a_yapClass, a_bytes));
		}
	}
}
