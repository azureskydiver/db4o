/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;

class YapClient extends YapStream implements ExtClient {
    final Object blobLock = new Object();

    private YapClientBlobThread blobThread;

    private YapSocket i_socket;
    Queue4 messageQueue = new Queue4();
    public final Lock4 messageQueueLock = new Lock4();

    private String password; // null denotes password not necessary

    int[] prefetchedIDs = new int[YapConst.PREFETCH_ID_COUNT];
    YapClientThread readerThread;
    int remainingIDs;
    private String switchedToFile;
    private boolean singleThreaded;

    private String userName;

    private Db4oDatabase i_db;

    private YapClient() {
        super(null);
    }

    /**
	 * Single-Threaded Client-Server Debug Mode
	 */
    public YapClient(String fakeServerFile) {
        this();
        synchronized (lock()) {
            singleThreaded = i_config.i_singleThreadedClient;
            if (Debug.fakeServer) {
                Debug.serverStream = (YapFile)Db4o.openFile(fakeServerFile);
                Debug.clientStream = this;
                Debug.clientMessageQueue = messageQueue;
                Debug.clientMessageQueueLock = messageQueueLock;
                readThis();
            } else {
                throw new RuntimeException("This constructor is for Debug.fakeServer use only.");
            }
            initialize3();
            Platform.postOpen(this);
        }
    }

    YapClient(YapSocket socket, String user, String password, boolean login) throws IOException {
        this();
        synchronized (lock()) {
            singleThreaded = i_config.i_singleThreadedClient;

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

            if (!singleThreaded) {
                readerThread = new YapClientThread(this, socket, messageQueue, messageQueueLock);
                readerThread.setName("db4o message client for user " + user);
                readerThread.start();
            }

            logMsg(36, toString());

            readThis();

            initialize3();
            Platform.postOpen(this);
        }
    }
    
    public void backup(String path)throws IOException{
        Db4o.throwRuntimeException(60);
    }

    boolean close2() {
        try {
            Msg.CLOSE.write(this, i_socket);
        } catch (Exception e) {
        }
        try {
            if (!singleThreaded) {
                readerThread.close();
            }
        } catch (Exception e) {
        }
        try {
            i_socket.close();
        } catch (Exception e) {
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

        YapSocket sock = null;

        // Using instanceof to keep the YapSocket class clean for C#
        if (i_socket instanceof YapSocketFake) {
            sock = ((YapSocketFake)i_socket).i_server.openFakeClientSocket();
        } else {
            sock = new YapSocket(i_socket.getHostName(), i_socket.getPort());
            loginToServer(sock);
        }

        if (switchedToFile != null) {
            MsgD message = Msg.SWITCH_TO_FILE.getWriterForString(i_systemTrans, switchedToFile);
            message.write(this, sock);
            if (! (Msg.OK.equals(Msg.readMessage(i_systemTrans, sock)))) {
                throw new IOException(Messages.get(42));
            }
        }
        Msg.USE_TRANSACTION.getWriterForInt(i_trans, serverThreadID).write(this, sock);
        return sock;
    }

    final QResult createQResult(Transaction a_ta) {
        return new QResultClient(a_ta);
    }

    final void createTransaction() {
        i_systemTrans = new TransactionClient(this, null);
        i_trans = new TransactionClient(this, i_systemTrans);
    }

    boolean createYapClass(YapClass a_yapClass, Class a_class, YapClass a_superYapClass) {
        writeMsg(Msg.CREATE_CLASS.getWriterForString(i_systemTrans, a_class.getName()));
        MsgObject message = (MsgObject)expectedResponse(Msg.OBJECT_TO_CLIENT);
        YapWriter bytes = message.unmarshall();
        if (bytes == null) {
            return false;
        }
        bytes.setTransaction(getSystemTransaction());
        if(! super.createYapClass(a_yapClass, a_class, a_superYapClass)){
            return false;
        }
        a_yapClass.setID(this, message.i_id);
        a_yapClass.readName1(getSystemTransaction(), bytes);
        i_classCollection.addYapClass(a_yapClass);
        i_classCollection.readYapClass(a_yapClass, a_class);
        return true;
    }
    
    long currentVersion(){
        writeMsg(Msg.CURRENT_VERSION);
        return ((MsgD)expectedResponse(Msg.ID_LIST)).readLong();
    }

    final boolean delete5(Transaction ta, YapObject yo, int a_cascade) {
        writeMsg(Msg.DELETE.getWriterForInt(i_trans, yo.getID()));
        return true;
    }

    boolean detectSchemaChanges() {
        return false;
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
                System.out.println(
                    "Unexpected Message:" + message + "  Expected:" + expectedMessage);
            }
        }
        return null;
    }

    final void free(int a_address, int a_length) {
        throw YapConst.virtualException();
    }

    void getAll(Transaction ta, QResult a_res) {
        writeMsg(Msg.GET_ALL);
        readResult(a_res);
    }

    /**
	 * may return null, if no message is returned. Error handling is weak and
	 * should ideally be able to trigger some sort of state listener
	 * (connection dead) on the client.
	 */
    Msg getResponse() {
        if (singleThreaded) {
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
        } else {

            return (Msg)messageQueueLock.run(new Closure4() {
                public Object run() {
                    Msg message = null;
                    message = (Msg)messageQueue.next();
                    if (message != null) {
                        if (Debug.messages) {
                            System.out.println(message + " processed at client");
                        }
                        return message;
                    }

                    if (readerThread.isClosed()) {
                        Db4o.throwRuntimeException(20, name());
                    }
                    messageQueueLock.snooze(i_config.i_timeoutClientSocket);
                    if (readerThread.isClosed()) {
                        Db4o.throwRuntimeException(20, name());
                    }
                    message = (Msg)messageQueue.next();
                    if (Debug.messages) {
                        System.out.println(message + " processed at client");
                    }
                    return message;
                }
            });
        }

    }
    
    YapClass getYapClass(int a_id) {
        YapClass yc = super.getYapClass(a_id);
        if(yc != null){
            return yc;
        }
        writeMsg(Msg.CLASS_NAME_FOR_ID.getWriterForInt(i_systemTrans, a_id));
        MsgD message = (MsgD) expectedResponse(Msg.CLASS_NAME_FOR_ID);
        String className = message.readString();
        if(className != null && className.length() > 0){
            try {
                Class clazz = Db4o.classForName(className);
                if(clazz != null){
                    return getYapClass(clazz, true);
                }
            } catch (ClassNotFoundException e) {
                // TODO inform client class not present
            }
        }
        return null;
    }


    final YapSocket agetYapSocket() {
        return i_socket;
    }

    boolean needsLockFileThread() {
        return false;
    }

    boolean hasShutDownHook() {
        return false;
    }
    
	public Db4oDatabase identity() {
	    if(i_db == null) {
	        writeMsg(Msg.IDENTITY);
	        YapWriter reader = expectedByteResponse(Msg.ID_LIST);
	        i_db = (Db4oDatabase)getByID(reader.readInt());
	        activate1(i_systemTrans, i_db, 3);
	    }
	    return i_db;
	}

    boolean isClient() {
        return true;
    }

    void loginToServer(YapSocket a_socket) throws IOException {
        if (password != null) {
            YapStringIOUnicode stringWriter = new YapStringIOUnicode();
            int length = stringWriter.length(userName) + stringWriter.length(password);
            
            MsgD message = Msg.LOGIN.getWriterForLength(i_systemTrans, length);
            message.writeString(userName);
            message.writeString(password);
            message.write(this, a_socket);
            if (! Msg.OK.equals(Msg.readMessage(i_systemTrans, a_socket))) {
                throw new IOException(Messages.get(42));
            }
        }
    }
    
    boolean maintainsIndices() {
        return false;
    }

    final YapWriter newObject(Transaction a_trans, YapMeta a_object) {
        throw YapConst.virtualException();
    }

    final int newUserObject() {
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

    int prefetchObjects(QResultClient qResult, Object[] prefetched, int prefetchCount) {

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
            writeMsg(Msg.READ_MULTIPLE_OBJECTS.getWriterForIntArray(i_trans, idsToGet, toGet));
            MsgD message = (MsgD)expectedResponse(Msg.READ_MULTIPLE_OBJECTS);
            int embeddedMessageCount = message.readInt();
            for (int i = 0; i < embeddedMessageCount; i++) {
                MsgObject mso = (MsgObject)Msg.OBJECT_TO_CLIENT.clone(qResult.i_trans);
                mso.payLoad = message.payLoad.readYapBytes();
                if (mso.payLoad != null) {
                    mso.payLoad.incrementOffset(YapConst.MESSAGE_LENGTH);
                    YapWriter reader = mso.unmarshall(YapConst.MESSAGE_LENGTH);
                    prefetched[position[i]] =
                        new YapObject(idsToGet[i]).readPrefetch(this, qResult.i_trans, reader);
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

    final void queryExecute(QQuery a_query, QResult a_res) {
        writeMsg(Msg.QUERY_EXECUTE.getWriter(marshall(a_query.getTransaction(), a_query)));
        readResult(a_res);
        a_res.reset();
    }
    
    void raiseVersion(long a_minimumVersion){
        writeMsg(Msg.RAISE_VERSION.getWriterForLong(i_trans, a_minimumVersion));
    }
    
    void readBytes(byte[] bytes, int address, int addressOffset, int length){
        YapConst.virtualException();
    }

    void readBytes(byte[] a_bytes, int a_address, int a_length) {
        writeMsg(Msg.READ_BYTES.getWriterFor2Ints(i_trans, a_address, a_length));
        YapWriter reader = expectedByteResponse(Msg.READ_BYTES);
        System.arraycopy(reader._buffer, 0, a_bytes, 0, a_length);
    }
    
    protected boolean rename1(Config4Impl config) {
        logMsg(58, null);
        return false;
    }

    final YapWriter readWriterByID(Transaction a_ta, int a_id) {
        try {
            writeMsg(Msg.READ_OBJECT.getWriterForInt(a_ta, a_id));
            YapWriter bytes = ((MsgObject)expectedResponse(Msg.OBJECT_TO_CLIENT)).unmarshall();
            bytes.setTransaction(a_ta);
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    final YapReader readReaderByID(Transaction a_ta, int a_id) {
        // TODO: read lightweight reader instead
        return readWriterByID(a_ta, a_id);
    }

    private void readResult(QResult aRes) {
        YapWriter reader = expectedByteResponse(Msg.ID_LIST);
        int size = reader.readInt();
        for (int i = 0; i < size; i++) {
            aRes.add(reader.readInt());
        }
    }

    void readThis() {
        writeMsg(Msg.GET_CLASSES.getWriter(i_systemTrans));
        YapWriter bytes = expectedByteResponse(Msg.GET_CLASSES);
        i_classCollection.setID(this, bytes.readInt());
        createStringIO(bytes.readByte());
        i_classCollection.read(i_systemTrans);
        i_classCollection.refreshClasses();
    }

    public void releaseSemaphore(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        writeMsg(Msg.RELEASE_SEMAPHORE.getWriterForString(i_trans, name));
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
        if (name == null) {
            throw new NullPointerException();
        }
        writeMsg(Msg.SET_SEMAPHORE.getWriterForIntString(i_trans, timeout, name));
        Msg message = getResponse();
        return (message.equals(Msg.SUCCESS));
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
        //        if(i_classCollection != null){
        //            return i_classCollection.toString();
        //        }
        return "Client Connection " + userName;
    }

    final YapWriter updateObject(Transaction a_trans, YapMeta a_object) {
        throw YapConst.virtualException();
    }

    void write(boolean shuttingDown) {
        // do nothing
    }

    final void writeDirty() {
        // do nothing
    }

    final void writeEmbedded(YapWriter a_parent, YapWriter a_child) {
        a_parent.addEmbedded(a_child);
    }
    
    final void writeMsg(Msg a_message){
        a_message.write(this, i_socket);
    }

    final void writeNew(YapClass a_yapClass, YapWriter aWriter) {
        writeMsg(Msg.WRITE_NEW.getWriter(a_yapClass, aWriter));
    }

    final void writeTransactionPointer(int a_address) {
        // do nothing
    }

    final void writeUpdate(YapClass a_yapClass, YapWriter a_bytes) {
        writeMsg(Msg.WRITE_UPDATE.getWriter(a_yapClass, a_bytes));
    }

}
