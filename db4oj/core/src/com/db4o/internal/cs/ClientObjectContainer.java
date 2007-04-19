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
public class ClientObjectContainer extends ObjectContainerBase implements ExtClient, BlobTransport, ClientMessageDispatcher {
	
	final Object blobLock = new Object();

	private BlobProcessor blobThread;

	private Socket4 i_socket;

	private BlockingQueue _messageQueue = new BlockingQueue();

	private String password; // null denotes password not necessary

	int[] _prefetchedIDs;

	ClientMessageDispatcher _messageDispatcher;

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

	private String _fakeServerFile;

	private boolean _login;

	/**
	 * Single-Threaded Client-Server Debug Mode, this constructor is only for
	 * fake server
	 */
	public ClientObjectContainer(String fakeServerFile) {
		super(Db4o.cloneConfiguration(), null);
		if (!Debug.fakeServer) {
			throw new IllegalStateException();
		}
		_fakeServerFile = fakeServerFile;
		open();
	}

	public ClientObjectContainer(Configuration config,Socket4 socket, String user, String password_, boolean login) {
		super(config, null);
		if (password_ == null) {
			throw new InvalidPasswordException();
		}
		userName = user;
		password = password_;
		i_socket = socket;
		_login = login;
		open();
	}

	protected final void openImpl() throws OpenDatabaseException {
		_singleThreaded = configImpl().singleThreadedClient();
		// TODO: Experiment with packetsize and noDelay
		// socket.setSendBufferSize(100);
		// socket.setTcpNoDelay(true);
		// System.out.println(socket.getSendBufferSize());
		if (Debug.fakeServer) {
			DebugCS.serverStream = (LocalObjectContainer) Db4o
					.openFile(_fakeServerFile);
			DebugCS.clientStream = this;
			DebugCS.clientMessageQueue = _messageQueue;
		} else {
			if (_login) {
				loginToServer(i_socket);
			}
			if (!_singleThreaded) {
				startDispatcherThread(i_socket, userName);
			}
			logMsg(36, toString());
			readThis();
		}
	}
	
	private void startDispatcherThread(Socket4 socket, String user) {
		_messageDispatcher = new ClientMessageDispatcherImpl(this, socket, _messageQueue);
		_messageDispatcher.setDispatcherName(user);
		_messageDispatcher.startDispatcher();
	}

	public void backup(String path) throws IOException {
		throw new UnsupportedOperationException();
	}
    
    public void blockSize(int blockSize){
        _blockSize = blockSize;
    }
    
    public byte blockSize() {
        return (byte)_blockSize;
    }

    protected void close2() {
		if (_messageDispatcher == null || !_messageDispatcher.isMessageDispatcherAlive()) {
			shutdownObjectContainer();
			return;
		}
		try {
			commit1();
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
				_messageDispatcher.close();
			}
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		try {
			i_socket.close();
		} catch (Exception e) {
			Exceptions4.catchAllExceptDb4oException(e);
		}
		
		shutdownObjectContainer();
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
			MsgD message = Msg.SWITCH_TO_FILE.getWriterForString(systemTransaction(),
					switchedToFile);
			message.write(this, sock);
			if (!(Msg.OK.equals(Msg.readMessage(this, systemTransaction(), sock)))) {
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
		writeMsg(Msg.CREATE_CLASS.getWriterForString(systemTransaction(), a_class
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
		bytes.setTransaction(systemTransaction());
		if (!super.createClassMetadata(a_yapClass, a_class, a_superYapClass)) {
			return false;
		}
		a_yapClass.setID(message.getId());
		a_yapClass.readName1(systemTransaction(), bytes);
		classCollection().addYapClass(a_yapClass);
		classCollection().readYapClass(a_yapClass, a_class);
		return true;
	}

	private void sendClassMeta(ReflectClass reflectClass) {
		ClassInfo classMeta = _classMetaHelper.getClassMeta(reflectClass);
		writeMsg(Msg.CLASS_META.getWriter(Serializer.marshall(systemTransaction(),classMeta)), true);
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
		checkDb4oExceptionMessage(message);
		throw new IllegalStateException("Unexpected Message:" + message
				+ "  Expected:" + expectedMessage);
	}

	private void checkDb4oExceptionMessage(Msg msg) {
		if(msg instanceof MDb4oException) {
			throw (Db4oException)((MDb4oException)msg).readSingleObject();
		}
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
	public Msg getResponse() {
		return _singleThreaded ? getResponseSingleThreaded()
				: getResponseMultiThreaded();
	}

	private Msg getResponseMultiThreaded() {
		Msg msg = (Msg)_messageQueue.next();
		if(msg == Msg.ERROR) {	
			onMsgError();
		}
		return msg;
	}
	
	private void onMsgError() {
		close();
		throw new Db4oException(Messages.get(Messages.CLOSED_OR_OPEN_FAILED));
	}

	
	private Msg getResponseSingleThreaded() {
		while (isMessageDispatcherAlive()) {
			try {
				final Msg message = Msg.readMessage(this, i_trans, i_socket);
				if(message instanceof ClientSideMessage) {
					if(((ClientSideMessage)message).processAtClient()){
						continue;
					}
				}
				return message;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public boolean isMessageDispatcherAlive() {
		return i_socket != null;
	}

	public ClassMetadata classMetadataForId(int a_id) {
		if(a_id == 0) {
			return null;
		}
		ClassMetadata yc = super.classMetadataForId(a_id);
		if (yc != null) {
			return yc;
		}
		MsgD msg = Msg.CLASS_NAME_FOR_ID.getWriterForInt(systemTransaction(), a_id);
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
				activate1(systemTransaction(), i_db, 3);
			} finally {
				showInternalClasses(false);
			}
		}
		return i_db;
	}

	public boolean isClient() {
		return true;
	}

	private void loginToServer(Socket4 a_socket)
			throws OpenDatabaseException,InvalidPasswordException {
		UnicodeStringIO stringWriter = new UnicodeStringIO();
		int length = stringWriter.length(userName)
				+ stringWriter.length(password);
		MsgD message = Msg.LOGIN
				.getWriterForLength(systemTransaction(), length);
		message.writeString(userName);
		message.writeString(password);
		message.write(this, a_socket);
		try {
			Msg msg = Msg.readMessage(this, systemTransaction(), a_socket);
			if (!Msg.LOGIN_OK.equals(msg)) {
				throw new InvalidPasswordException();
			}
			Buffer payLoad = msg.payLoad();
			_blockSize = payLoad.readInt();
			int doEncrypt = payLoad.readInt();
			if (doEncrypt == 0) {
				i_handlers.oldEncryptionOff();
			}
		} catch (IOException e) {
			throw new OpenDatabaseException(e);
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
		MsgD msg = Msg.READ_OBJECT.getWriterForInt(a_ta, a_id);
		writeMsg(msg, true);
		StatefulBuffer bytes = ((MsgObject) expectedResponse(Msg.OBJECT_TO_CLIENT))
				.unmarshall();
		if(bytes != null){
			bytes.setTransaction(a_ta);
		}
		return bytes;
	}

	public final StatefulBuffer[] readWritersByIDs(Transaction a_ta, int[] ids) {
		try {
			MsgD msg = Msg.READ_MULTIPLE_OBJECTS.getWriterForIntArray(a_ta, ids, ids.length);
			writeMsg(msg, true);
			MsgD response = (MsgD) expectedResponse(Msg.READ_MULTIPLE_OBJECTS);
			int count = response.readInt();
			StatefulBuffer[] yapWriters = new StatefulBuffer[count];
			for (int i = 0; i < count; i++) {
				MsgObject mso = (MsgObject) Msg.OBJECT_TO_CLIENT.publicClone();
				mso.setTransaction(getTransaction());
				mso.payLoad(response.payLoad().readYapBytes());
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
		writeMsg(Msg.GET_CLASSES.getWriter(systemTransaction()), true);
		Buffer bytes = expectedByteResponse(Msg.GET_CLASSES);
		classCollection().setID(bytes.readInt());
		createStringIO(bytes.readByte());
		classCollection().read(systemTransaction());
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
				writeMsg(Msg.USER_MESSAGE.getWriter(Serializer.marshall(i_trans,obj)), true);
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

	public void shutdown() {
		// do nothing
	}

	public final void writeDirty() {
		// do nothing
	}

	public final void writeEmbedded(StatefulBuffer a_parent, StatefulBuffer a_child) {
		a_parent.addEmbedded(a_child);
	}

	public final void write(Msg msg) {
		writeMsg(msg, true);
	}
	
	public final void writeMsg(Msg a_message, boolean flush) {
		if(i_config.batchMessages()) {
			if(flush && _batchedMessages.isEmpty()) {
				// if there's nothing batched, just send this message directly
				writeImpl(a_message);
			} else {
				addToBatch(a_message);
				if(flush || _batchedQueueLength > i_config.maxBatchQueueSize()) {
					writeBatchedMessages();
				}
			}
		} else {
			writeImpl(a_message);
		}
	}

	private void writeImpl(Msg msg) {
		msg.write(this, i_socket);
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
			write(Msg.PING);
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
		MsgD msg = Msg.QUERY_EXECUTE.getWriter(Serializer.marshall(trans,query));
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
		writeImpl(multibytes);
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

	int timeout() {
		return isEmbeddedClient()
			// TODO: make CLIENT_EMBEDDED_TIMEOUT configurable
			? Const4.CLIENT_EMBEDDED_TIMEOUT
			: configImpl().timeoutClientSocket();
	}

	private boolean isEmbeddedClient() {
		return i_socket instanceof LoopbackSocket;
	}

	protected void shutdownDataStorage() {
		// do nothing here
	}

	public void setDispatcherName(String name) {
		// do nothing here		
	}

	public void startDispatcher() {
		// do nothing here for single thread, ClientObjectContainer is already running
	}
	
	public ClientMessageDispatcher messageDispatcher() {
		return _singleThreaded ? this : _messageDispatcher;
	}

	public void onCommittedListener() {
		if(_singleThreaded) {
			return;
		}
		write(Msg.COMMITTED_CALLBACK_REGISTER);
	}
	
}
