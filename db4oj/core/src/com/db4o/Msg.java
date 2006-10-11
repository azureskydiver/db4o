/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.inside.query.QueryResult;

/**
 * Messages for Client/Server Communication
 */
class Msg implements Cloneable {

	static int _idGenerator = 1;
	private static Msg[] _messages = new Msg[60];

	int _msgID;
	String _name;
	Transaction _trans;

	public static final MsgD CLASS_NAME_FOR_ID = new MClassNameForID();
	public static final Msg CLOSE = new Msg("CLOSE");
    public static final Msg COMMIT = new MCommit();
    public static final Msg COMMIT_OK = new MCommitOK();
	public static final MsgD CREATE_CLASS = new MCreateClass();
	public static final Msg CURRENT_VERSION = new Msg("VERSION");
	public static final MsgD DELETE = new MDelete();
	public static final Msg ERROR = new Msg("ERROR");
	public static final Msg FAILED = new Msg("FAILED");
	public static final Msg GET_ALL = new MGetAll();
	public static final MsgD GET_CLASSES = new MGetClasses();
	public static final MsgD GET_INTERNAL_IDS = new MGetInternalIDs();
	public static final Msg GET_THREAD_ID = new Msg("GET_THREAD_ID");
	public static final MsgD ID_LIST = new MsgD("ID_LIST");
	public static final Msg IDENTITY = new Msg("IDENTITY");
	public static final MsgD LENGTH = new MsgD("LENGTH");
    public static final MsgD LOGIN = new MsgD("LOGIN");
    public static final MsgD LOGIN_OK = new MsgD("LOGIN_OK");
	public static final Msg NULL = new Msg("NULL");
	public static final MsgD OBJECT_BY_UUID = new MObjectByUuid();
	public static final MsgObject OBJECT_TO_CLIENT = new MsgObject();
	public static final Msg OK = new Msg("OK");
	public static final Msg PING = new Msg("PING");
	public static final MsgD PREFETCH_IDS = new MPrefetchIDs();
	public static final MsgObject QUERY_EXECUTE = new MQueryExecute();
	public static final MsgD RAISE_VERSION = new MsgD("RAISE_VERSION");
	public static final MsgBlob READ_BLOB = new MReadBlob();
	public static final MsgD READ_BYTES = new MReadBytes();
	public static final MsgD READ_MULTIPLE_OBJECTS = new MReadMultipleObjects();
	public static final MsgD READ_OBJECT = new MReadObject();
	public static final MsgD RELEASE_SEMAPHORE = new MReleaseSemaphore();
	public static final Msg ROLLBACK = new MRollback();
	public static final MsgD SET_SEMAPHORE = new MSetSemaphore();
	public static final Msg SUCCESS = new Msg("SUCCESS");
	public static final MsgD SWITCH_TO_FILE = new MsgD("SWITCH_F");
	public static final Msg SWITCH_TO_MAIN_FILE = new Msg("SWITCH_M");
	public static final Msg TA_BEGIN_END_SET = new MTaBeginEndSet();
	public static final MsgD TA_DELETE = new MTaDelete();
	public static final MsgD TA_DONT_DELETE = new MTaDontDelete();
	public static final MsgD TA_IS_DELETED = new MTaIsDeleted();
	public static final MsgD USER_MESSAGE = new MUserMessage();
	public static final MsgD USE_TRANSACTION = new MUseTransaction();
	public static final MsgBlob WRITE_BLOB = new MWriteBlob();
	public static final MWriteNew WRITE_NEW = new MWriteNew();
	public static final MsgObject WRITE_UPDATE = new MWriteUpdate();
	public static final MsgD WRITE_UPDATE_DELETE_MEMBERS = new MWriteUpdateDeleteMembers();

	Msg() {
		_msgID = _idGenerator++;
		_messages[_msgID] = this;
	}

	Msg(String aName) {
		this();
		_name = aName;
	}
	
	final Msg clone(Transaction a_trans) {
		Msg msg = null;
		try {
			msg=(Msg) clone();
			msg._trans = a_trans;
		} catch (CloneNotSupportedException e) {
			// shouldn't happen
		}
		return msg;
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
	

	void fakePayLoad(Transaction a_trans) {
	    _trans = a_trans;
		// do nothing
	}

	/**
	 * dummy method to allow clean override handling
	 * without casting
	 */
	YapReader getByteLoad() {
		return null;
	}

	final String getName() {
		if (_name == null) {
			return getClass().getName();
		}
		return _name;
	}

	Transaction getTransaction() {
		return _trans;
	}
	
	YapStream getStream(){
	    return getTransaction().stream();
	}

	/**
	 * server side execution
	 */
	boolean processMessageAtServer(YapSocket socket) {
		// default: do nothing
		return false; // since not processed
	}

	static final Msg readMessage(Transaction a_trans, YapSocket sock) throws IOException {
		YapWriter reader = new YapWriter(a_trans, YapConst.MESSAGE_LENGTH);
		if(!reader.read(sock)) {
			return null;
		}
		Msg message = _messages[reader.readInt()].readPayLoad(a_trans, sock, reader);
		if (Debug.messages) {
			System.out.println(message + " arrived at " + a_trans.stream());
		}
		return message;
	}

	Msg readPayLoad(Transaction a_trans, YapSocket sock, YapReader reader)
		throws IOException {
	    if(reader.readByte() == YapConst.SYSTEM_TRANS && a_trans.i_parentTransaction != null){
	        a_trans = a_trans.i_parentTransaction;
	    }
	    return clone(a_trans);
	}

	final void setTransaction(Transaction aTrans) {
		_trans = aTrans;
	}

	final public String toString() {
		return getName();
	}

	final void write(YapStream stream, YapSocket sock) {
		if (Debug.fakeServer) {
		    YapStream i_stream = null;
			if (stream == Debug.serverStream) {
				i_stream = Debug.clientStream;
			} else {
				i_stream = Debug.serverStream;
			}
			setTransaction(i_stream.getTransaction());
			fakePayLoad(i_stream.getTransaction());
			if (stream == Debug.serverStream) {
				final Object finalThis = this;
                try{
    				Debug.clientMessageQueueLock.run(new Closure4() {
                        public Object run() {
    						Debug.clientMessageQueue.add(finalThis);
                            return null;
                        }
                    });
                }catch(Exception ex){
                    
                    // TODO: notify client app about problems and try to fix here
                    
                }
			} else {
				processMessageAtServer(null);
			}
		} else {
			synchronized (sock) {
				try {
					if (Debug.messages) {
						System.out.println(this +" sent by " + stream);
					}
					sock.write(getPayLoad()._buffer);
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

	YapWriter getPayLoad() {
		YapWriter writer = new YapWriter(getTransaction(), YapConst.MESSAGE_LENGTH);
		writer.writeInt(_msgID);
		return writer;
	}

	final void writeQueryResult(Transaction a_trans, QueryResult qr, YapSocket sock) {
		int size = qr.size();
		MsgD message = ID_LIST.getWriterForLength(a_trans, YapConst.ID_LENGTH * (size + 1));
		YapWriter writer = message.getPayLoad();
		writer.writeQueryResult(qr);
		message.write(a_trans.stream(), sock);
	}
}