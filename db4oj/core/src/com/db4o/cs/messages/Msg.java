/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import java.io.*;
import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;

/**
 * Messages for Client/Server Communication
 */
public class Msg implements Cloneable {

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
	public static final MsgD GET_ALL = new MGetAll();
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
	public static final MsgD OBJECTSET_FETCH = new MObjectSetFetch();
	public static final MsgD OBJECTSET_FINALIZED = new MsgD("OBJECTSET_FINALIZED");
	public static final MsgD OBJECTSET_GET_ID = new MObjectSetGetId();
	public static final MsgD OBJECTSET_INDEXOF = new MObjectSetIndexOf();
	public static final MsgD OBJECTSET_RESET = new MObjectSetReset();
	public static final MsgD OBJECTSET_SIZE = new MObjectSetSize();
	public static final Msg OK = new Msg("OK");
	public static final Msg PING = new Msg("PING");
	public static final MsgD PREFETCH_IDS = new MPrefetchIDs();
	public static final MsgObject QUERY_EXECUTE = new MQueryExecute();
	public static final MsgD QUERY_RESULT = new MsgD("QUERY_RESULT");
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
	
	public final Msg clone(Transaction a_trans) {
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
	public YapReader getByteLoad() {
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
	
	protected YapStream stream(){
	    return transaction().stream();
	}
	
	protected Object streamLock(){
		return stream().lock();
	}
	
	protected Config4Impl config(){
		return stream().config();
	}

	/**
	 * server side execution
	 * @param serverThread TODO
	 */
	public boolean processAtServer(YapServerThread serverThread) {
		// default: do nothing
		return false; // since not processed
	}

	public static final Msg readMessage(Transaction a_trans, YapSocket sock) throws IOException {
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
	    a_trans = checkParentTransaction(a_trans, reader);
	    return clone(a_trans);
	}

	protected Transaction checkParentTransaction(Transaction a_trans, YapReader reader) {
		if(reader.readByte() == YapConst.SYSTEM_TRANS && a_trans.parentTransaction() != null){
	        return a_trans.parentTransaction();
	    }
		return a_trans;
	}

	final void setTransaction(Transaction aTrans) {
		_trans = aTrans;
	}

	final public String toString() {
		return getName();
	}
	

	public final void write(YapStream stream, YapSocket sock) {
		if (Debug.fakeServer) {
		    YapStream i_stream = null;
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
				processAtServer(null);
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

	public YapWriter payLoad() {
		YapWriter writer = new YapWriter(transaction(), YapConst.MESSAGE_LENGTH);
		writer.writeInt(_msgID);
		return writer;
	}

}