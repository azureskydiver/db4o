/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.IOException;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.inside.Exceptions4;
import com.db4o.reflect.ReflectClass;

/**
 * @exclude
 */
public class YapClient extends YapStream implements ExtClient {
	final Object blobLock = new Object();

	private YapClientBlobThread blobThread;

	private YapSocket i_socket;

	Queue4 messageQueue = new Queue4();

	final Lock4 messageQueueLock = new Lock4();

	private String password; // null denotes password not necessary

	int[] prefetchedIDs = new int[YapConst.PREFETCH_ID_COUNT];

	private YapClientThread _readerThread;

	int remainingIDs;

	private String switchedToFile;

	private boolean _singleThreaded;

	private String userName;

	private Db4oDatabase i_db;

	private boolean _doFinalize=true;
    
    private byte _blockSize = 1;
    

	private YapClient() {
		super(null);
	}

	/**
	 * Single-Threaded Client-Server Debug Mode
	 */
	public YapClient(String fakeServerFile) {
		this();
		synchronized (lock()) {
			_singleThreaded = configImpl().singleThreadedClient();
			if (Debug.fakeServer) {
				Debug.serverStream = (YapFile) Db4o.openFile(fakeServerFile);
				Debug.clientStream = this;
				Debug.clientMessageQueue = messageQueue;
				Debug.clientMessageQueueLock = messageQueueLock;
				readThis();
			} else {
				throw new RuntimeException(
						"This constructor is for Debug.fakeServer use only.");
			}
			initialize3();
			Platform4.postOpen(this);
		}
	}

	YapClient(YapSocket socket, String user, String password, boolean login)
			throws IOException {
		this();
		synchronized (lock()) {
			_singleThreaded = configImpl().singleThreadedClient();

			// TODO: Experiment with packetsize and noDelay
			// socket.setSendBufferSize(100);
			// socket.setTcpNoDelay(true);
			// System.out.println(socket.getSendBufferSize());

			if (password == null) {
				throw new NullPointerException(Messages.get(56));
			}
			if (!login) {
				password = null;
			}

			userName = user;
			this.password = password;
			i_socket = socket;
			try {
				loginToServer(socket);
			} catch (IOException e) {
				i_references.stopTimer();
				throw e;
			}

			if (!_singleThreaded) {
				startReaderThread(socket, user);
			}

			logMsg(36, toString());

			readThis();

			initialize3();
			Platform4.postOpen(this);
		}
	}

	private void startReaderThread(YapSocket socket, String user) {
		_readerThread = new YapClientThread(this, socket, messageQueue,
				messageQueueLock);
		_readerThread.setName("db4o message client for user " + user);
		_readerThread.start();
	}

	public void backup(String path) throws IOException {
		Exceptions4.throwRuntimeException(60);
	}
    
    public byte blockSize() {
        return _blockSize;
    }

	boolean close2() {
		if (null != _readerThread && _readerThread.isClosed()) {
			//throw new Db4oException("Connection is already closed.");
			return true;
		}
		try {
			Msg.COMMIT_OK.write(this, i_socket);
			expectedResponse(Msg.OK);
		} catch (Exception e) {
			Exceptions4.catchAll(e);
		}
		try {
			Msg.CLOSE.write(this, i_socket);
		} catch (Exception e) {
			Exceptions4.catchAll(e);
		}
		try {
			if (!_singleThreaded) {
				_readerThread.close();
			}
		} catch (Exception e) {
			Exceptions4.catchAll(e);
		}
		try {
			i_socket.close();
		} catch (Exception e) {
			Exceptions4.catchAll(e);
		}
		boolean ret = super.close2();
		if (Debug.fakeServer) {
			Debug.serverStream.close();
		}
		return ret;
	}

	final void commit1() {
		i_trans.commit();
	}

	final ClassIndex createClassIndex(YapClass a_yapClass) {
		return new ClassIndexClient(a_yapClass);
	}

	YapSocket createParalellSocket() throws IOException {
		Msg.GET_THREAD_ID.write(this, i_socket);
		int serverThreadID = expectedByteResponse(Msg.ID_LIST).readInt();

		YapSocket sock = i_socket.openParalellSocket();

		if (!(i_socket instanceof YapSocketFake)) {
			loginToServer(sock);
		}

		if (switchedToFile != null) {
			MsgD message = Msg.SWITCH_TO_FILE.getWriterForString(i_systemTrans,
					switchedToFile);
			message.write(this, sock);
			if (!(Msg.OK.equals(Msg.readMessage(i_systemTrans, sock)))) {
				throw new IOException(Messages.get(42));
			}
		}
		Msg.USE_TRANSACTION.getWriterForInt(i_trans, serverThreadID).write(
				this, sock);
		return sock;
	}

	final QueryResultImpl createQResult(Transaction a_ta) {
		return new QResultClient(a_ta);
	}

	final void createTransaction() {
		i_systemTrans = new TransactionClient(this, null);
		i_trans = new TransactionClient(this, i_systemTrans);
	}

	boolean createYapClass(YapClass a_yapClass, ReflectClass a_class,
			YapClass a_superYapClass) {
		writeMsg(Msg.CREATE_CLASS.getWriterForString(i_systemTrans, a_class
				.getName()));
		Msg resp = getResponse();
		if (resp == null) {
			return false;
		}
		if (resp.equals(Msg.FAILED)) {
			if (configImpl().exceptionsOnNotStorable()) {
				throw new ObjectNotStorableException(a_class);
			}
			return false;
		}
		if (!resp.equals(Msg.OBJECT_TO_CLIENT)) {
			return false;
		}

		MsgObject message = (MsgObject) resp;
		YapWriter bytes = message.unmarshall();
		if (bytes == null) {
			return false;
		}
		bytes.setTransaction(getSystemTransaction());
		if (!super.createYapClass(a_yapClass, a_class, a_superYapClass)) {
			return false;
		}
		a_yapClass.setID(message._id);
		a_yapClass.readName1(getSystemTransaction(), bytes);
		i_classCollection.addYapClass(a_yapClass);
		i_classCollection.readYapClass(a_yapClass, a_class);
		return true;
	}

	public long currentVersion() {
		writeMsg(Msg.CURRENT_VERSION);
		return ((MsgD) expectedResponse(Msg.ID_LIST)).readLong();
	}

	final boolean delete5(Transaction ta, YapObject yo, int a_cascade,
			boolean userCall) {
		writeMsg(Msg.DELETE.getWriterForInts(i_trans, new int[] { yo.getID(),
				userCall ? 1 : 0 }));
		return true;
	}

	boolean detectSchemaChanges() {
		return false;
	}

	protected boolean doFinalize() {
		return _doFinalize;
	}
	
	final YapWriter expectedByteResponse(Msg expectedMessage) {
		Msg msg = expectedResponse(expectedMessage);
		if (msg == null) {
			// TODO: throw Exception to allow
			// smooth shutdown
			return null;
		}
		return msg.getByteLoad();
	}

	final Msg expectedResponse(Msg expectedMessage) {
		Msg message = getResponse();
		if (expectedMessage.equals(message)) {
			return message;
		}
		if (Deploy.debug) {
			new RuntimeException().printStackTrace();
			if (message == null) {
				System.out.println("Message was null");
			}
			if (!expectedMessage.equals(message)) {
				System.out.println("Unexpected Message:" + message
						+ "  Expected:" + expectedMessage);
			}
		}
		return null;
	}

	final void free(int a_address, int a_length) {
		throw Exceptions4.virtualException();
	}

	void getAll(Transaction ta, QueryResultImpl a_res) {
		writeMsg(Msg.GET_ALL);
		readResult(a_res);
	}

	/**
	 * may return null, if no message is returned. Error handling is weak and
	 * should ideally be able to trigger some sort of state listener (connection
	 * dead) on the client.
	 */
	Msg getResponse() {
		return _singleThreaded ? getResponseSingleThreaded()
				: getResponseMultiThreaded();
	}

	private Msg getResponseMultiThreaded() {
		try {

			return (Msg) messageQueueLock.run(new Closure4() {
				public Object run() {
					Msg message = retrieveMessage();
					if (message != null) {
						return message;
					}

					throwOnClosed();
					messageQueueLock.snooze(configImpl().timeoutClientSocket());
					throwOnClosed();
					return retrieveMessage();
				}

				private void throwOnClosed() {
					// FIXME: Should _readerThread ever be null here?
					if (_readerThread==null||_readerThread.isClosed()) {
						_doFinalize=false;
						Exceptions4.throwRuntimeException(20, name());
					}
				}

				private Msg retrieveMessage() {
					Msg message = null;
					message = (Msg) messageQueue.next();
					if (message != null) {
						if (Debug.messages) {
							System.out
									.println(message + " processed at client");
						}
						if (Msg.ERROR.equals(message)) {
							throw new Db4oException("Client connection error");
						}
					}
					return message;
				}
			});
		} catch (Exception ex) {
			Exceptions4.catchAll(ex);
			return null;
		}
	}

	private Msg getResponseSingleThreaded() {
		while (i_socket != null) {
			try {
				final Msg message = Msg.readMessage(i_trans, i_socket);
				if (Msg.PING.equals(message)) {
					writeMsg(Msg.OK);
				} else if (Msg.CLOSE.equals(message)) {
					logMsg(35, toString());
					close();
					return null;
				} else if (message != null) {
					return message;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	public YapClass getYapClass(int a_id) {
		YapClass yc = super.getYapClass(a_id);
		if (yc != null) {
			return yc;
		}
		writeMsg(Msg.CLASS_NAME_FOR_ID.getWriterForInt(i_systemTrans, a_id));
		MsgD message = (MsgD) expectedResponse(Msg.CLASS_NAME_FOR_ID);
		String className = message.readString();
		if (className != null && className.length() > 0) {
			ReflectClass claxx = reflector().forName(className);
			if (claxx != null) {
				return getYapClass(claxx, true);
			}
			// TODO inform client class not present
		}
		return null;
	}

	boolean needsLockFileThread() {
		return false;
	}

	boolean hasShutDownHook() {
		return false;
	}

	public Db4oDatabase identity() {
		if (i_db == null) {
			writeMsg(Msg.IDENTITY);
			YapWriter reader = expectedByteResponse(Msg.ID_LIST);
			showInternalClasses(true);
			i_db = (Db4oDatabase) getByID(reader.readInt());
			activate1(i_systemTrans, i_db, 3);
			showInternalClasses(false);
		}
		return i_db;
	}

	public boolean isClient() {
		return true;
	}

	void loginToServer(YapSocket a_socket) throws IOException {
		if (password != null) {
			YapStringIOUnicode stringWriter = new YapStringIOUnicode();
			int length = stringWriter.length(userName)
					+ stringWriter.length(password);

			MsgD message = Msg.LOGIN.getWriterForLength(i_systemTrans, length);
			message.writeString(userName);
			message.writeString(password);
			message.write(this, a_socket);
            Msg msg = Msg.readMessage(i_systemTrans, a_socket);
			if (!Msg.LOGIN_OK.equals(msg)) {
				throw new IOException(Messages.get(42));
			}
            YapWriter payLoad = msg.getPayLoad();
            _blockSize = (byte)payLoad.readInt();
            int doEncrypt = payLoad.readInt();
            if(doEncrypt == 0){
                i_handlers.oldEncryptionOff();
            }
            
		}
	}

	boolean maintainsIndices() {
		return false;
	}

	public final int newUserObject() {
		YapWriter reader = null;
		if (remainingIDs < 1) {
			writeMsg(Msg.PREFETCH_IDS);
			reader = expectedByteResponse(Msg.ID_LIST);
			for (int i = YapConst.PREFETCH_ID_COUNT - 1; i >= 0; i--) {
				prefetchedIDs[i] = reader.readInt();
			}
			remainingIDs = YapConst.PREFETCH_ID_COUNT;
		}
		remainingIDs--;
		return prefetchedIDs[remainingIDs];
	}

	int prefetchObjects(QResultClient qResult, Object[] prefetched,
			int prefetchCount) {

		int count = 0;

		int toGet = 0;
		int[] idsToGet = new int[prefetchCount];
		int[] position = new int[prefetchCount];

		while (qResult.hasNext() && (count < prefetchCount)) {
			boolean foundInCache = false;
			int id = qResult.nextInt();
			if (id > 0) {
				YapObject yo = getYapObject(id);
				if (yo != null) {
					Object candidate = yo.getObject();
					if (candidate != null) {
						prefetched[count] = candidate;
						foundInCache = true;
					} else {
						yapObjectGCd(yo);
					}
				}
				if (!foundInCache) {
					idsToGet[toGet] = id;
					position[toGet] = count;
					toGet++;
				}
				count++;
			}
		}

		if (toGet > 0) {
			writeMsg(Msg.READ_MULTIPLE_OBJECTS.getWriterForIntArray(i_trans,
					idsToGet, toGet));
			MsgD message = (MsgD) expectedResponse(Msg.READ_MULTIPLE_OBJECTS);
			int embeddedMessageCount = message.readInt();
			for (int i = 0; i < embeddedMessageCount; i++) {
				MsgObject mso = (MsgObject) Msg.OBJECT_TO_CLIENT
						.clone(qResult.i_trans);
				mso._payLoad = message._payLoad.readYapBytes();
				if (mso._payLoad != null) {
					mso._payLoad.incrementOffset(YapConst.MESSAGE_LENGTH);
					YapWriter reader = mso.unmarshall(YapConst.MESSAGE_LENGTH);
					prefetched[position[i]] = new YapObject(idsToGet[i])
							.readPrefetch(this, qResult.i_trans, reader);
				}
			}
		}
		return count;
	}

	void processBlobMessage(MsgBlob msg) {
		synchronized (blobLock) {
			boolean needStart = blobThread == null || blobThread.isTerminated();
			if (needStart) {
				blobThread = new YapClientBlobThread(this);
			}
			blobThread.add(msg);
			if (needStart) {
				blobThread.start();
			}
		}
	}

	final void queryExecute(QQuery a_query, QueryResultImpl a_res) {
		writeMsg(Msg.QUERY_EXECUTE.getWriter(marshall(a_query.getTransaction(),
				a_query)));
		readResult(a_res);
	}

	public void raiseVersion(long a_minimumVersion) {
		writeMsg(Msg.RAISE_VERSION.getWriterForLong(i_trans, a_minimumVersion));
	}

	void readBytes(byte[] bytes, int address, int addressOffset, int length) {
		throw Exceptions4.virtualException();
	}

	void readBytes(byte[] a_bytes, int a_address, int a_length) {
		writeMsg(Msg.READ_BYTES.getWriterForInts(i_trans, new int[] {
				a_address, a_length }));
		YapWriter reader = expectedByteResponse(Msg.READ_BYTES);
		System.arraycopy(reader._buffer, 0, a_bytes, 0, a_length);
	}

	protected boolean rename1(Config4Impl config) {
		logMsg(58, null);
		return false;
	}

	public final YapWriter readWriterByID(Transaction a_ta, int a_id) {
		try {
			writeMsg(Msg.READ_OBJECT.getWriterForInt(a_ta, a_id));
			YapWriter bytes = ((MsgObject) expectedResponse(Msg.OBJECT_TO_CLIENT))
					.unmarshall();
			if (bytes == null) {
				return null;
			}
			bytes.setTransaction(a_ta);
			return bytes;
		} catch (Exception e) {
			return null;
		}
	}

	public final YapReader readReaderByID(Transaction a_ta, int a_id) {
		// TODO: read lightweight reader instead
		return readWriterByID(a_ta, a_id);
	}

	private void readResult(QueryResultImpl aRes) {
		YapWriter reader = expectedByteResponse(Msg.ID_LIST);
		int size = reader.readInt();
		for (int i = 0; i < size; i++) {
			aRes.add(reader.readInt());
		}
		aRes.reset();
	}

	void readThis() {
		writeMsg(Msg.GET_CLASSES.getWriter(i_systemTrans));
		YapWriter bytes = expectedByteResponse(Msg.GET_CLASSES);
		i_classCollection.setID(bytes.readInt());
		createStringIO(bytes.readByte());
		i_classCollection.read(i_systemTrans);
		i_classCollection.refreshClasses();
	}

	public void releaseSemaphore(String name) {
		synchronized (i_lock) {
			checkClosed();
			if (name == null) {
				throw new NullPointerException();
			}
			writeMsg(Msg.RELEASE_SEMAPHORE.getWriterForString(i_trans, name));
		}
	}

	void releaseSemaphores(Transaction ta) {
		// do nothing
	}

	private void reReadAll() {
		remainingIDs = 0;
		initialize0();
		initialize1();
		createTransaction();
		readThis();
	}

	final void rollback1() {
		writeMsg(Msg.ROLLBACK);
		i_trans.rollback();
	}

	public void send(Object obj) {
		synchronized (i_lock) {
			if (obj != null) {
				writeMsg(Msg.USER_MESSAGE.getWriter(marshall(i_trans, obj)));
			}
		}
	}

	final void setDirty(UseSystemTransaction a_object) {
		// do nothing
	}

	public boolean setSemaphore(String name, int timeout) {
		synchronized (i_lock) {
			checkClosed();
			if (name == null) {
				throw new NullPointerException();
			}
			writeMsg(Msg.SET_SEMAPHORE.getWriterForIntString(i_trans, timeout,
					name));
			Msg message = getResponse();
			return (message.equals(Msg.SUCCESS));
		}
	}

	public void switchToFile(String fileName) {
		synchronized (i_lock) {
			commit();
			writeMsg(Msg.SWITCH_TO_FILE.getWriterForString(i_trans, fileName));
			expectedResponse(Msg.OK);
			reReadAll();
			switchedToFile = fileName;
		}
	}

	public void switchToMainFile() {
		synchronized (i_lock) {
			commit();
			writeMsg(Msg.SWITCH_TO_MAIN_FILE);
			expectedResponse(Msg.OK);
			reReadAll();
			switchedToFile = null;
		}
	}

	public String name() {
		return toString();
	}

	public String toString() {
		// if(i_classCollection != null){
		// return i_classCollection.toString();
		// }
		return "Client Connection " + userName;
	}

	void write(boolean shuttingDown) {
		// do nothing
	}

	final void writeDirty() {
		// do nothing
	}

	public final void writeEmbedded(YapWriter a_parent, YapWriter a_child) {
		a_parent.addEmbedded(a_child);
	}

	final void writeMsg(Msg a_message) {
		a_message.write(this, i_socket);
	}

	public final void writeNew(YapClass a_yapClass, YapWriter aWriter) {
		writeMsg(Msg.WRITE_NEW.getWriter(a_yapClass, aWriter));
	}
    
    final void writeObject(YapMeta a_object, YapReader a_writer, int address) {
        Exceptions4.shouldNeverBeCalled();
    }

	final void writeTransactionPointer(int a_address) {
		// do nothing
	}

	public final void writeUpdate(YapClass a_yapClass, YapWriter a_bytes) {
		writeMsg(Msg.WRITE_UPDATE.getWriter(a_yapClass, a_bytes));
	}

	public boolean isAlive() {
		try {
			writeMsg(Msg.PING);
			return expectedResponse(Msg.OK) != null;
		} catch (Db4oException exc) {
			return false;
		}
	}

	// Remove, for testing purposes only
	public YapSocket socket() {
		return i_socket;
	}
}
