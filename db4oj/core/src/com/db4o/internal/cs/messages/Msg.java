/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;

/**
 * Messages for Client/Server Communication
 */
public abstract class Msg implements Cloneable {

	static int _idGenerator = 1;
	private static Msg[] _messages = new Msg[60];

	int _msgID;
	String _name;
	private Transaction _trans;
	private MessageDispatcher _messageDispatcher;
	

	public static final MsgD CLASS_NAME_FOR_ID = new MClassNameForID();
	public static final Msg CLOSE = new MClose();
    public static final Msg COMMIT = new MCommit();
    public static final MsgD COMMIT_RESPONSE = new MCommitResponse();
    public static final Msg COMMIT_SYSTEMTRANS = new MCommitSystemTransaction();
	public static final MsgD CREATE_CLASS = new MCreateClass();
	public static final MsgObject CLASS_META = new MClassMeta();
	public static final Msg CURRENT_VERSION = new MVersion();
	public static final MsgD DELETE = new MDelete();
	public static final Msg ERROR = new MError();
	public static final Msg FAILED = new MFailed();
	public static final MsgD GET_ALL = new MGetAll();
	public static final MsgD GET_CLASSES = new MGetClasses();
	public static final MsgD GET_INTERNAL_IDS = new MGetInternalIDs();
	public static final Msg GET_THREAD_ID = new MGetThreadID();
	public static final MsgD ID_LIST = new MIDList();
	public static final Msg IDENTITY = new MIdentity();
	public static final MsgD LENGTH = new MLength();
    public static final MsgD LOGIN = new MLogin();
    public static final MsgD LOGIN_OK = new MLoginOK();
	public static final Msg NULL = new MNull();
	public static final MsgD OBJECT_BY_UUID = new MObjectByUuid();
	public static final MsgObject OBJECT_TO_CLIENT = new MsgObject();
	public static final MsgD OBJECTSET_FETCH = new MObjectSetFetch();
	public static final MsgD OBJECTSET_FINALIZED = new MObjectSetFinalized();
	public static final MsgD OBJECTSET_GET_ID = new MObjectSetGetId();
	public static final MsgD OBJECTSET_INDEXOF = new MObjectSetIndexOf();
	public static final MsgD OBJECTSET_RESET = new MObjectSetReset();
	public static final MsgD OBJECTSET_SIZE = new MObjectSetSize();
	public static final Msg OK = new MOK();
	public static final Msg PING = new MPing();
	public static final MsgD PREFETCH_IDS = new MPrefetchIDs();
	public static final Msg PROCESS_DELETES = new MProcessDeletes();
	public static final MsgObject QUERY_EXECUTE = new MQueryExecute();
	public static final MsgD QUERY_RESULT = new MsgD("QUERY_RESULT");
	public static final MsgD RAISE_VERSION = new MRaiseVersion();
	public static final MsgBlob READ_BLOB = new MReadBlob();
	public static final MsgD READ_BYTES = new MReadBytes();
	public static final MsgD READ_MULTIPLE_OBJECTS = new MReadMultipleObjects();
	public static final MsgD READ_OBJECT = new MReadObject();
	public static final MsgD RELEASE_SEMAPHORE = new MReleaseSemaphore();
	public static final Msg ROLLBACK = new MRollback();
	public static final MsgD SET_SEMAPHORE = new MSetSemaphore();
	public static final Msg SUCCESS = new MSuccess();
	public static final MsgD SWITCH_TO_FILE = new MSwitchToFile();
	public static final Msg SWITCH_TO_MAIN_FILE = new MSwitchToMainFile();
	public static final MsgD TA_DELETE = new MTaDelete();
	public static final MsgD TA_IS_DELETED = new MTaIsDeleted();
	public static final MsgD USER_MESSAGE = new MUserMessage();
	public static final MsgD USE_TRANSACTION = new MUseTransaction();
	public static final MsgBlob WRITE_BLOB = new MWriteBlob();
	public static final MWriteNew WRITE_NEW = new MWriteNew();
	public static final MsgObject WRITE_UPDATE = new MWriteUpdate();
	public static final MsgD WRITE_UPDATE_DELETE_MEMBERS = new MWriteUpdateDeleteMembers();
	public static final MWriteBatchedMessages WRITE_BATCHED_MESSAGES = new MWriteBatchedMessages();

	Msg() {
		_msgID = _idGenerator++;
		_messages[_msgID] = this;
	}

	Msg(String aName) {
		this();
		_name = aName;
	}
	
	public static Msg getMessage(int id) {
		return _messages[id];
	}
	
	public final Msg publicClone() {
		try {
			return (Msg)clone();
		} catch (CloneNotSupportedException e) {
			Exceptions4.shouldNeverHappen();
			return null;
		}
	}
	
	public final boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||obj.getClass()!=this.getClass()) {
			return false;
		}
	    return _msgID == ((Msg) obj)._msgID;
	}
	
	public int hashCode() {
		return _msgID;
	}

	void fakePayLoad(Transaction a_trans) {
	    _trans = a_trans;
		// do nothing
	}

	/**
	 * dummy method to allow clean override handling
	 * without casting
	 */
	public Buffer getByteLoad() {
		return null;
	}

	final String getName() {
		if (_name == null) {
			return getClass().getName();
		}
		return _name;
	}

	protected Transaction transaction() {
		return _trans;
	}
	
	protected LocalObjectContainer file(){
		return (LocalObjectContainer)stream();
	}
	
	protected ObjectContainerBase stream(){
	    return transaction().stream();
	}
	
	protected Object streamLock(){
		return stream().lock();
	}
	
	protected Config4Impl config(){
		return stream().config();
	}
	
    protected static StatefulBuffer readMessageBuffer(Transaction trans, Socket4 sock) throws IOException {
		return readMessageBuffer(trans, sock, Const4.MESSAGE_LENGTH);
    }

	protected static StatefulBuffer readMessageBuffer(Transaction trans, Socket4 sock, int length) throws IOException {
		StatefulBuffer buffer = new StatefulBuffer(trans, length);		
        int offset = 0;
        while (length > 0) {
            int read = sock.read(buffer._buffer, offset, length);
			if(read<0) {
				return null;
			}
            offset += read;
            length -= read;
        }
		return buffer;
	}


	public static final Msg readMessage(MessageDispatcher messageDispatcher, Transaction trans, Socket4 sock) throws IOException {
		StatefulBuffer reader = readMessageBuffer(trans, sock);
		if (null == reader) {
			return null;
		}
		Msg message = _messages[reader.readInt()].readPayLoad(messageDispatcher, trans, sock, reader);
		if (Debug.messages) {
			System.out.println(message + " arrived at " + trans.stream());
		}
		return message;
	}

	Msg readPayLoad(MessageDispatcher messageDispatcher, Transaction a_trans, Socket4 sock, Buffer reader)
		throws IOException {
		Msg msg = publicClone();
		msg.setMessageDispatcher(messageDispatcher);
		msg.setTransaction(checkParentTransaction(a_trans, reader));
	    return msg;
	}

	protected final Transaction checkParentTransaction(Transaction a_trans, Buffer reader) {
		if(reader.readByte() == Const4.SYSTEM_TRANS && a_trans.parentTransaction() != null){
	        return a_trans.parentTransaction();
	    }
		return a_trans;
	}

	public final void setTransaction(Transaction aTrans) {
		_trans = aTrans;
	}

	final public String toString() {
		return getName();
	}
	

	public void write(Msg msg) {
		_messageDispatcher.write(msg);
	}
	
	public void respondInt(int response){
    	write(Msg.ID_LIST.getWriterForInt(transaction(), response));
    }
	
	public final void write(ObjectContainerBase stream, Socket4 sock) {
		if (null == stream) {
			throw new ArgumentNullException();
		}
		if (null == sock) {
			throw new ArgumentNullException();
		}
		if (Debug.fakeServer) {
		    ObjectContainerBase i_stream = null;
			if (stream == DebugCS.serverStream) {
				i_stream = DebugCS.clientStream;
			} else {
				i_stream = DebugCS.serverStream;
			}
			setTransaction(i_stream.getTransaction());
			fakePayLoad(i_stream.getTransaction());
			if (stream == DebugCS.serverStream) {
				final Object finalThis = this;
                try{
    				DebugCS.clientMessageQueueLock.run(new Closure4() {
                        public Object run() {
    						DebugCS.clientMessageQueue.add(finalThis);
                            return null;
                        }
                    });
                }catch(Exception ex){
                    
                    // TODO: notify client app about problems and try to fix here
                    
                }
			} else {
				((ServerSideMessage)this).processAtServer();
			}
		} else {
			synchronized (sock) {
				try {
					if (Debug.messages) {
						System.out.println(this +" sent by " + stream);
					}
					sock.write(payLoad()._buffer);
					sock.flush();
				} catch (Exception e) {
                    
                    // Note: It is not sufficient to catch IoException only
                    // here. .NET will throw different exceptions if a socket
                    // is disconnected.
                    
					// TODO: handle more softly in YapClient, maybe
					// try to reconnect
                    
				}
			}
		}
	}

	public StatefulBuffer payLoad() {
		StatefulBuffer writer = new StatefulBuffer(transaction(), Const4.MESSAGE_LENGTH);
		writer.writeInt(_msgID);
		return writer;
	}

	
	public MessageDispatcher messageDispatcher() {
		return _messageDispatcher;
	}
	
	public ServerMessageDispatcher serverMessageDispatcher() {
		if(_messageDispatcher instanceof ServerMessageDispatcher) {
			return (ServerMessageDispatcher) _messageDispatcher;	
		}
		throw new IllegalStateException();
	}

	public ClientMessageDispatcher clientMessageDispatcher() {
		if(_messageDispatcher instanceof ClientMessageDispatcher) {
			return (ClientMessageDispatcher) _messageDispatcher;	
		}
		throw new IllegalStateException();
	}
	
	public void setMessageDispatcher(MessageDispatcher messageDispatcher) {
		_messageDispatcher = messageDispatcher;
	}
	
	public void logMsg(int msgCode, String msg) {
		stream().logMsg(msgCode, msg);
	}

}