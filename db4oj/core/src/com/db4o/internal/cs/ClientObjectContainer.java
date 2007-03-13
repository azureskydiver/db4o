/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.cs.messages.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class ClientObjectContainer extends ObjectContainerBase implements ExtClient, BlobTransport {
	
	final Object blobLock = new Object();

	private BlobProcessor blobThread;

	private Socket4 i_socket;

	Queue4 messageQueue = new Queue4();

	final Lock4 messageQueueLock = new Lock4();

	private String password; // null denotes password not necessary

	int[] _prefetchedIDs;

	private ClientMessageDispatcher _readerThread;

	int remainingIDs;

	private String switchedToFile;

	private boolean _singleThreaded;

	private String userName;

	private Db4oDatabase i_db;

	protected boolean _doFinalize=true;
    
    private int _blockSize = 1;
    
	private Collection4 _batchedMessages = new Collection4();
	
	// initial value of _batchedQueueLength is YapConst.INT_LENGTH, which is
	// used for to write the number of messages.
	private int _batchedQueueLength = Const4.INT_LENGTH;

	private ClientObjectContainer(Configuration config) {
		super(config,null);
	}

	/**
	 * Single-Threaded Client-Server Debug Mode
	 */
	public ClientObjectContainer(String fakeServerFile) {
		this(Db4o.cloneConfiguration());
		synchronized (lock()) {
			_singleThreaded = configImpl().singleThreadedClient();
			if (Debug.fakeServer) {
				DebugCS.serverStream = (LocalObjectContainer) Db4o.openFile(fakeServerFile);
				DebugCS.clientStream = this;
				DebugCS.clientMessageQueue = messageQueue;
				DebugCS.clientMessageQueueLock = messageQueueLock;
				readThis();
			} else {
				throw new RuntimeException(
						"This constructor is for Debug.fakeServer use only.");
			}
			initializePostOpen();
			Platform4.postOpen(this);
		}
	}

	public ClientObjectContainer(Configuration config,Socket4 socket, String user, String password_, boolean login)
			throws IOException {
		this(config);
		synchronized (lock()) {
			_singleThreaded = configImpl().singleThreadedClient();

			// TODO: Experiment with packetsize and noDelay
			// socket.setSendBufferSize(100);
			// socket.setTcpNoDelay(true);
			// System.out.println(socket.getSendBufferSize());

			if (password_ == null) {
				throw new ArgumentNullException(Messages.get(56));
			}
			if (!login) {
				password_ = null;
			}

			userName = user;
			password = password_;
			i_socket = socket;
			try {
				loginToServer(socket);
			} catch (IOException e) {
				stopSession();
				throw e;
			}

			if (!_singleThreaded) {
				startReaderThread(socket, user);
			}

			logMsg(36, toString());

			readThis();

			initializePostOpen();
			Platform4.postOpen(this);
		}
	}

	private void startReaderThread(Socket4 socket, String user) {
		_readerThread = new ClientMessageDispatcher(this, socket, messageQueue,
				messageQueueLock);
		_readerThread.setName("db4o message client for user " + user);
		_readerThread.start();
	}

	public void backup(String path) throws IOException {
		Exceptions4.throwRuntimeException(60);
	}
    
    public void blockSize(int blockSize){
        _blockSize = blockSize;
    }
    
    public byte blockSize() {
        return (byte)_blockSize;
    }

    protected void close2() {
		if (_readerThread == null || _readerThread.isClosed()) {
			super.close2();
			return;
		}
		try {
			writeMsg(Msg.COMMIT_OK, true);
			expectedResponse(Msg.OK);
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		try {
			writeMsg(Msg.CLOSE, true);
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		try {
			if (!_singleThreaded) {
				_readerThread.close();
			}
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		try {
			i_socket.close();
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		
		super.close2();
		if (Debug.fakeServer) {
			DebugCS.serverStream.close();
		}
	}

	public final void commit1() {
		i_trans.commit();
	}
    
    public int converterVersion() {
        return Converter.VERSION;
    }
	
	Socket4 createParalellSocket() throws IOException {
		writeMsg(Msg.GET_THREAD_ID, true);
		
		int serverThreadID = expectedByteResponse(Msg.ID_LIST).readInt();

		Socket4 sock = i_socket.openParalellSocket();

		if (!(i_socket instanceof LoopbackSocket)) {
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

	public AbstractQueryResult newQueryResult(Transaction trans, QueryEvaluationMode mode) {
		throw new IllegalStateException();
	}

	final public Transaction newTransaction(Transaction parentTransaction) {
		return new ClientTransaction(this, parentTransaction);
	}

	public boolean createClassMetadata(ClassMetadata a_yapClass, ReflectClass a_class,
			ClassMetadata a_superYapClass) {
		writeMsg(Msg.CREATE_CLASS.getWriterForString(i_systemTrans, a_class
				.getName()), true);
		Msg resp = getResponse();
		if (resp == null) {
			return false;
		}
		
		if (resp.equals(Msg.FAILED)) {
			// if the class can not be created on the server, send class meta to the server.
			sendClassMeta(a_class);
			resp = getResponse();
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
		StatefulBuffer bytes = message.unmarshall();
		if (bytes == null) {
			return false;
		}
		bytes.setTransaction(getSystemTransaction());
		if (!super.createClassMetadata(a_yapClass, a_class, a_superYapClass)) {
			return false;
		}
		a_yapClass.setID(message.getId());
		a_yapClass.readName1(getSystemTransaction(), bytes);
		classCollection().addYapClass(a_yapClass);
		classCollection().readYapClass(a_yapClass, a_class);
		return true;
	}

	private void sendClassMeta(ReflectClass reflectClass) {
		ClassInfo classMeta = _classMetaHelper.getClassMeta(reflectClass);
		writeMsg(Msg.CLASS_META.getWriter(marshall(i_systemTrans, classMeta)), true);
	}
	
	public long currentVersion() {
		writeMsg(Msg.CURRENT_VERSION, true);
		return ((MsgD) expectedResponse(Msg.ID_LIST)).readLong();
	}

	public final boolean delete4(Transaction ta, ObjectReference yo, int a_cascade, boolean userCall) {
		MsgD msg = Msg.DELETE.getWriterForInts(i_trans, new int[] { yo.getID(), userCall ? 1 : 0 });
		writeMsg(msg, false);
		return true;
	}

	public boolean detectSchemaChanges() {
		return false;
	}

	protected boolean doFinalize() {
		return _doFinalize;
	}
	
	final Buffer expectedByteResponse(Msg expectedMessage) {
		Msg msg = expectedResponse(expectedMessage);
		if (msg == null) {
			// TODO: throw Exception to allow
			// smooth shutdown
			return null;
		}
		return msg.getByteLoad();
	}

	public final Msg expectedResponse(Msg expectedMessage) {
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

	public AbstractQueryResult getAll(Transaction trans) {
		int mode = config().queryEvaluationMode().asInt();
		MsgD msg = Msg.GET_ALL.getWriterForInt(trans, mode);
		writeMsg(msg, true);
		return readQueryResult(trans);
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
					messageQueueLock.snooze(timeout());
					throwOnClosed();
					return retrieveMessage();
				}

				private void throwOnClosed() {
					if (_readerThread.isClosed()) {
						_doFinalize=false;
						throw new Db4oException(Messages.get(Messages.CLOSED_OR_OPEN_FAILED));
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
			Exceptions4.catchAllExceptDb4oException(ex);
			return null;
		}
	}

	private Msg getResponseSingleThreaded() {
		while (i_socket != null) {
			try {
				final Msg message = Msg.readMessage(i_trans, i_socket);
				if (Msg.PING.equals(message)) {
					writeMsg(Msg.OK, true);
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

	public ClassMetadata classMetadataForId(int a_id) {
		if(a_id == 0) {
			return null;
		}
		ClassMetadata yc = super.classMetadataForId(a_id);
		if (yc != null) {
			return yc;
		}
		MsgD msg = Msg.CLASS_NAME_FOR_ID.getWriterForInt(i_systemTrans, a_id);
		writeMsg(msg, true);
		MsgD message = (MsgD) expectedResponse(Msg.CLASS_NAME_FOR_ID);
		String className = message.readString();
		if (className != null && className.length() > 0) {
			ReflectClass claxx = reflector().forName(className);
			if (claxx != null) {
				return produceClassMetadata(claxx);
			}
			// TODO inform client class not present
		}
		return null;
	}

	public boolean needsLockFileThread() {
		return false;
	}

	protected boolean hasShutDownHook() {
		return false;
	}

	public Db4oDatabase identity() {
		if (i_db == null) {
			writeMsg(Msg.IDENTITY, true);
			Buffer reader = expectedByteResponse(Msg.ID_LIST);
			showInternalClasses(true);
			try {
				i_db = (Db4oDatabase) getByID(reader.readInt());
				activate1(i_systemTrans, i_db, 3);
			} finally {
				showInternalClasses(false);
			}
		}
		return i_db;
	}

	public boolean isClient() {
		return true;
	}

	void loginToServer(Socket4 a_socket) throws IOException {
		if (password != null) {
			UnicodeStringIO stringWriter = new UnicodeStringIO();
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
            Buffer payLoad = msg.payLoad();
            _blockSize = payLoad.readInt();
            int doEncrypt = payLoad.readInt();
            if(doEncrypt == 0){
                i_handlers.oldEncryptionOff();
            }
            
		}
	}

	public boolean maintainsIndices() {
		return false;
	}

	public final int newUserObject() {
		int prefetchIDCount = config().prefetchIDCount();
		ensureIDCacheAllocated(prefetchIDCount);
		Buffer reader = null;
		if (remainingIDs < 1) {
			MsgD msg = Msg.PREFETCH_IDS.getWriterForInt(i_trans, prefetchIDCount);
			writeMsg(msg, true);
			reader = expectedByteResponse(Msg.ID_LIST);
			for (int i = prefetchIDCount - 1; i >= 0; i--) {
				_prefetchedIDs[i] = reader.readInt();
			}
			remainingIDs = prefetchIDCount;
		}
		remainingIDs--;
		return _prefetchedIDs[remainingIDs];
	}

	void processBlobMessage(MsgBlob msg) {
		synchronized (blobLock) {
			boolean needStart = blobThread == null || blobThread.isTerminated();
			if (needStart) {
				blobThread = new BlobProcessor(this);
			}
			blobThread.add(msg);
			if (needStart) {
				blobThread.start();
			}
		}
	}

	public void raiseVersion(long a_minimumVersion) {
		writeMsg(Msg.RAISE_VERSION.getWriterForLong(i_trans, a_minimumVersion), true);
	}

	public void readBytes(byte[] bytes, int address, int addressOffset, int length) {
		throw Exceptions4.virtualException();
	}

	public void readBytes(byte[] a_bytes, int a_address, int a_length) {
		MsgD msg = Msg.READ_BYTES.getWriterForInts(i_trans, new int[] {
				a_address, a_length });
		writeMsg(msg, true);
		Buffer reader = expectedByteResponse(Msg.READ_BYTES);
		System.arraycopy(reader._buffer, 0, a_bytes, 0, a_length);
	}

	protected boolean rename1(Config4Impl config) {
		logMsg(58, null);
		return false;
	}

	public final StatefulBuffer readWriterByID(Transaction a_ta, int a_id) {
		try {
			MsgD msg = Msg.READ_OBJECT.getWriterForInt(a_ta, a_id);
			writeMsg(msg, true);
			StatefulBuffer bytes = ((MsgObject) expectedResponse(Msg.OBJECT_TO_CLIENT))
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

	public final StatefulBuffer[] readWritersByIDs(Transaction a_ta, int[] ids) {
		try {
			MsgD msg = Msg.READ_MULTIPLE_OBJECTS.getWriterForIntArray(a_ta, ids, ids.length);
			writeMsg(msg, true);
			MsgD message = (MsgD) expectedResponse(Msg.READ_MULTIPLE_OBJECTS);
			int count = message.readInt();
			StatefulBuffer[] yapWriters = new StatefulBuffer[count];
			for (int i = 0; i < count; i++) {
				MsgObject mso = (MsgObject) Msg.OBJECT_TO_CLIENT.clone(getTransaction());
				mso.payLoad(message.payLoad().readYapBytes());
				if (mso.payLoad() != null) {
					mso.payLoad().incrementOffset(Const4.MESSAGE_LENGTH);
					yapWriters[i] = mso.unmarshall(Const4.MESSAGE_LENGTH);
					yapWriters[i].setTransaction(a_ta);
				}
			}
			return yapWriters;
		} catch (Exception e) {
			if(Debug.atHome) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public final Buffer readReaderByID(Transaction a_ta, int a_id) {
		// TODO: read lightweight reader instead
		return readWriterByID(a_ta, a_id);
	}

	private AbstractQueryResult readQueryResult(Transaction trans) {
		AbstractQueryResult queryResult = null;
		Buffer reader = expectedByteResponse(Msg.QUERY_RESULT);
		int queryResultID = reader.readInt();
		if(queryResultID > 0){
			queryResult = new LazyClientQueryResult(trans, this, queryResultID);
		}else{
			queryResult = new ClientQueryResult(trans);
		}
		queryResult.loadFromIdReader(reader);
		return queryResult;
	}

	void readThis() {
		writeMsg(Msg.GET_CLASSES.getWriter(i_systemTrans), true);
		Buffer bytes = expectedByteResponse(Msg.GET_CLASSES);
		classCollection().setID(bytes.readInt());
		createStringIO(bytes.readByte());
		classCollection().read(i_systemTrans);
		classCollection().refreshClasses();
	}

	public void releaseSemaphore(String name) {
		synchronized (i_lock) {
			checkClosed();
			if (name == null) {
				throw new NullPointerException();
			}
			writeMsg(Msg.RELEASE_SEMAPHORE.getWriterForString(i_trans, name), true);
		}
	}

	public void releaseSemaphores(Transaction ta) {
		// do nothing
	}

	private void reReadAll(Configuration config) {
		remainingIDs = 0;
		initialize1(config);
		initializeTransactions();
		readThis();
	}

	public final void rollback1() {
		if (i_config.batchMessages()) {
			clearBatchedObjects();
		} 
		writeMsg(Msg.ROLLBACK, true);
		i_trans.rollback();
	}

	public void send(Object obj) {
		synchronized (i_lock) {
			if (obj != null) {
				writeMsg(Msg.USER_MESSAGE.getWriter(marshall(i_trans, obj)), true);
			}
		}
	}

	public final void setDirtyInSystemTransaction(PersistentBase a_object) {
		// do nothing
	}

	public boolean setSemaphore(String name, int timeout) {
		synchronized (i_lock) {
			checkClosed();
			if (name == null) {
				throw new NullPointerException();
			}
			MsgD msg = Msg.SET_SEMAPHORE.getWriterForIntString(i_trans,
					timeout, name);
			writeMsg(msg, true);
			Msg message = getResponse();
			return (message.equals(Msg.SUCCESS));
		}
	}

	public void switchToFile(String fileName) {
		synchronized (i_lock) {
			commit();
			MsgD msg = Msg.SWITCH_TO_FILE.getWriterForString(i_trans, fileName);
			writeMsg(msg, true);
			expectedResponse(Msg.OK);
			// FIXME NSC
			reReadAll(Db4o.cloneConfiguration());
			switchedToFile = fileName;
		}
	}

	public void switchToMainFile() {
		synchronized (i_lock) {
			commit();
			writeMsg(Msg.SWITCH_TO_MAIN_FILE, true);
			expectedResponse(Msg.OK);
			// FIXME NSC
			reReadAll(Db4o.cloneConfiguration());
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

	public void write(boolean shuttingDown) {
		// do nothing
	}

	public final void writeDirty() {
		// do nothing
	}

	public final void writeEmbedded(StatefulBuffer a_parent, StatefulBuffer a_child) {
		a_parent.addEmbedded(a_child);
	}

	final void writeMsg(Msg a_message) {
		a_message.write(this, i_socket);
	}
	
	public final void writeMsg(Msg a_message, boolean flush) {
		if(i_config.batchMessages()) {
			if(flush && _batchedMessages.isEmpty()) {
				// if there's nothing batched, just send this message directly
				writeMsg(a_message);
			} else {
				addToBatch(a_message);
				if(flush || _batchedQueueLength > i_config.maxBatchQueueSize()) {
					writeBatchedMessages();
				}
			}
		} else {
			writeMsg(a_message);
		}
	}

	public final void writeNew(ClassMetadata a_yapClass, StatefulBuffer aWriter) {
		MsgD msg = Msg.WRITE_NEW.getWriter(a_yapClass, aWriter);
		writeMsg(msg, false);
	}
    
	public final void writeTransactionPointer(int a_address) {
		// do nothing
	}

	public final void writeUpdate(ClassMetadata a_yapClass, StatefulBuffer a_bytes) {
		MsgD msg = Msg.WRITE_UPDATE.getWriter(a_yapClass, a_bytes);
		writeMsg(msg, false);
	}

	public boolean isAlive() {
		try {
			writeMsg(Msg.PING, true);
			return expectedResponse(Msg.OK) != null;
		} catch (Db4oException exc) {
			return false;
		}
	}

	// Remove, for testing purposes only
	public Socket4 socket() {
		return i_socket;
	}
	
	private void ensureIDCacheAllocated(int prefetchIDCount) {
		if(_prefetchedIDs==null) {
			_prefetchedIDs = new int[prefetchIDCount];
			return;
		}
		if(prefetchIDCount>_prefetchedIDs.length) {
			int[] newPrefetchedIDs=new int[prefetchIDCount];
			System.arraycopy(_prefetchedIDs, 0, newPrefetchedIDs, 0, _prefetchedIDs.length);
			_prefetchedIDs=newPrefetchedIDs;
		}
	}

    public SystemInfo systemInfo() {
        throw new NotImplementedException("Functionality not availble on clients.");
    }

	
    public void writeBlobTo(Transaction trans, BlobImpl blob, File file) throws IOException {
        MsgBlob msg = (MsgBlob) Msg.READ_BLOB.getWriterForInt(trans, (int) getID(blob));
        msg._blob = blob;
        processBlobMessage(msg);
    }
    
    public void readBlobFrom(Transaction trans, BlobImpl blob, File file) throws IOException {
        MsgBlob msg = null;
        synchronized (lock()) {
            set(blob);
            int id = (int) getID(blob);
            msg = (MsgBlob) Msg.WRITE_BLOB.getWriterForInt(trans, id);
            msg._blob = blob;
            blob.setStatus(Status.QUEUED);
        }
        processBlobMessage(msg);
    }
    
    public long[] getIDsForClass(Transaction trans, ClassMetadata clazz){
    	MsgD msg = Msg.GET_INTERNAL_IDS.getWriterForInt(trans, clazz.getID());
    	writeMsg(msg, true);
    	Buffer reader = expectedByteResponse(Msg.ID_LIST);
    	int size = reader.readInt();
    	final long[] ids = new long[size];
    	for (int i = 0; i < size; i++) {
    	    ids[i] = reader.readInt();
    	}
    	return ids;
    }
    
    public QueryResult classOnlyQuery(Transaction trans, ClassMetadata clazz){
        long[] ids = clazz.getIDs(trans); 
        ClientQueryResult resClient = new ClientQueryResult(trans, ids.length);
        for (int i = 0; i < ids.length; i++) {
            resClient.add((int)ids[i]);
        }
        return resClient;
    }
    
    public QueryResult executeQuery(QQuery query){
    	Transaction trans = query.getTransaction();
    	query.evaluationMode(config().queryEvaluationMode());
        query.marshall();
		MsgD msg = Msg.QUERY_EXECUTE.getWriter(marshall(trans,query));
		writeMsg(msg, true);
		return readQueryResult(trans);
    }

    public final void writeBatchedMessages() {
		if (_batchedMessages.isEmpty()) {
			return;
		}

		Msg msg;
		MsgD multibytes = Msg.WRITE_BATCHED_MESSAGES.getWriterForLength(
				getTransaction(), _batchedQueueLength);
		multibytes.writeInt(_batchedMessages.size());
		Iterator4 iter = _batchedMessages.iterator();
		while(iter.moveNext()) {
			msg = (Msg) iter.current();
			if (msg == null) {
				multibytes.writeInt(0);
			} else {
				multibytes.writeInt(msg.payLoad().getLength());
				multibytes.payLoad().append(msg.payLoad()._buffer);
			}
		}
		writeMsg(multibytes);
		clearBatchedObjects();
	}

	public final void addToBatch(Msg msg) {
		_batchedMessages.add(msg);
		// the first INT_LENGTH is for buffer.length, and then buffer content.
		_batchedQueueLength += Const4.INT_LENGTH + msg.payLoad().getLength();
	}

	private final void clearBatchedObjects() {
		_batchedMessages.clear();
		// initial value of _batchedQueueLength is YapConst.INT_LENGTH, which is
		// used for to write the number of messages.
		_batchedQueueLength = Const4.INT_LENGTH;
	}

	private int timeout() {
		return isEmbeddedClient()
			// TODO: make CLIENT_EMBEDDED_TIMEOUT configurable
			? Const4.CLIENT_EMBEDDED_TIMEOUT
			: configImpl().timeoutClientSocket();
	}

	private boolean isEmbeddedClient() {
		return i_socket instanceof LoopbackSocket;
	}

}
