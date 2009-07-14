/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal;

import com.db4o.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.events.*;

public final class ServerMessageDispatcherImpl extends Thread implements ServerMessageDispatcher {

    private String _clientName;

    private boolean _loggedin;
    
    private boolean _closeMessageSent;

    private final ObjectServerImpl _server;

    private Socket4 _socket;

    private ClientTransactionHandle _transactionHandle;
    
    private Hashtable4 _queryResults;
    
    final int _threadID;

	private CallbackObjectInfoCollections _committedInfo;

	private boolean _caresAboutCommitted;
	
	private boolean _isClosed;
	
	private final Object _lock = new Object();
	
	private final Object _mainLock;

	private final Event4Impl _messageReceived = new Event4Impl();
	
    ServerMessageDispatcherImpl(ObjectServerImpl server,
			ClientTransactionHandle transactionHandle, Socket4 socket,
			int threadID, boolean loggedIn, Object mainLock) throws Exception {

    	_mainLock = mainLock;
		_transactionHandle = transactionHandle;

		setDaemon(true);

		_loggedin = loggedIn;

		_server = server;
		_threadID = threadID;
		setDispatcherName("" + threadID);
		_socket = socket;
		_socket.setSoTimeout(((Config4Impl) server.configure())
				.timeoutServerSocket());

		// TODO: Experiment with packetsize and noDelay
		// i_socket.setSendBufferSize(100);
		// i_socket.setTcpNoDelay(true);
	}

    public boolean close() {
        synchronized(_lock) {
            if (!isMessageDispatcherAlive()) {
                return true;
            }
            _isClosed = true;
        }
    	synchronized(_mainLock) {
			_transactionHandle.releaseTransaction();
			sendCloseMessage();
			_transactionHandle.close();
			closeSocket();
			removeFromServer();
			return true;
    	}
	}

    public void closeConnection() {
        synchronized (_lock) {
			if (!isMessageDispatcherAlive()) {
				return;
			}
			_isClosed = true;
        }
        synchronized (_mainLock) {
			closeSocket();
			removeFromServer();
		}
	}
    
    public boolean isMessageDispatcherAlive() {
        synchronized(_lock){
            return !_isClosed;
        }
    }

	private void sendCloseMessage() {
		try {
            if (! _closeMessageSent) {
                _closeMessageSent = true;
                write(Msg.CLOSE);
            }
        } catch (Exception e) {
            if (Debug4.atHome) {
                e.printStackTrace();
            }
        }
	}

	private void removeFromServer() {
		try {
            _server.removeThread(this);
        } catch (Exception e) {
            if (Debug4.atHome) {
                e.printStackTrace();
            }
        }
	}

	private void closeSocket() {
		try {
			if(_socket != null) {
				_socket.close();
			}
        } catch (Db4oIOException e) {
            if (Debug4.atHome) {
                e.printStackTrace();
            }
        }
	}

	public Transaction getTransaction() {
    	return _transactionHandle.transaction();
    }

	public void run() {
		try{
			messageLoop();
		}finally{
			close();
		}
	}
    
    private void messageLoop(){
        while (isMessageDispatcherAlive()) {
            try {
                if(! messageProcessor()){
                    return;
                }
            } catch (Db4oIOException e) {
            	if(DTrace.enabled){
            		DTrace.ADD_TO_CLASS_INDEX.log(e.toString());
            	}
                return;
            }
        }
    }
    
    private boolean messageProcessor() throws Db4oIOException{
        Msg message = Msg.readMessage(this, getTransaction(), _socket);
        if(message == null){
            return true;
        }
        
        triggerMessageReceived(message);
        
        if(!_loggedin && !Msg.LOGIN.equals(message)) {
        	return true;
        }

        // TODO: COR-885 - message may process against closed server
        // Checking aliveness just makes the issue less likely to occur. Naive synchronization against main lock is prohibitive.        
    	if(isMessageDispatcherAlive()) {
    		try {
    			return ((ServerSideMessage)message).processAtServer();
    		}
			catch (OutOfMemoryError oome){
				writeException(message, new InternalServerError(oome));
				return true;
			}
			catch (RuntimeException exc) {
				writeException(message, exc);
				return true;
			} 
    	}
    	return false;
    }

	private void writeException(Msg message, Exception exc) {
		if(!(message instanceof MessageWithResponse)) {
			exc.printStackTrace();
			return;
		}
		if(!(exc instanceof RuntimeException)) {
			exc = new Db4oException(exc);
		}
		message.writeException((RuntimeException)exc);
	}

    private void triggerMessageReceived(Message message) {
    	ServerPlatform.triggerMessageEvent(_messageReceived, message);
    }

	public ObjectServerImpl server() {
    	return _server;
    }
    
	public void queryResultFinalized(int queryResultID) {
    	_queryResults.remove(queryResultID);
	}

	public void mapQueryResultToID(LazyClientObjectSetStub stub, int queryResultID) {
    	if(_queryResults == null){
    		_queryResults = new Hashtable4();
    	}
    	_queryResults.put(queryResultID, stub);
	}
	
	public LazyClientObjectSetStub queryResultForID(int queryResultID){
		return (LazyClientObjectSetStub) _queryResults.get(queryResultID);
	}

	public void switchToFile(MSwitchToFile message) {
        synchronized (_mainLock) {
            String fileName = message.readString();
            try {
                _transactionHandle.releaseTransaction();
            	_transactionHandle.acquireTransactionForFile(fileName);
                write(Msg.OK);
            } catch (Exception e) {
                if (Debug4.atHome) {
                    System.out.println("Msg.SWITCH_TO_FILE failed.");
                    e.printStackTrace();
                }
                _transactionHandle.releaseTransaction();
                write(Msg.ERROR);
            }
        }
    }

    public void switchToMainFile() {
        synchronized (_mainLock) {
            _transactionHandle.releaseTransaction();
            write(Msg.OK);
        }
    }

    public void useTransaction(MUseTransaction message) {
        int threadID = message.readInt();
		Transaction transToUse = _server.findTransaction(threadID);
		_transactionHandle.transaction(transToUse);
    }
    
    public boolean write(Msg msg){
    	synchronized(_lock) {
    	    if(! isMessageDispatcherAlive()){
    	        return false;
    	    }
    		return msg.write(_socket);
    	}
    }
    
    public Socket4 socket(){
    	return _socket;
    }

	public String name() {
		return _clientName;
	}
	
	public void setDispatcherName(String name) {
		_clientName = name;
		// set thread name
		setName("db4o server message dispatcher " + name);
	}
    
    public int dispatcherID() {
    	return _threadID;
    }

	public void login() {
		_loggedin = true;
	}

	public void startDispatcher() {
		start();
	}

	public boolean caresAboutCommitted() {
		return _caresAboutCommitted;
	}

	public void caresAboutCommitted(boolean care) {
		_caresAboutCommitted = true;
        server().checkCaresAboutCommitted();
	}

	public CallbackObjectInfoCollections committedInfo() {
		return _committedInfo;
	}

	public void dispatchCommitted(CallbackObjectInfoCollections committedInfo) {
		_committedInfo = committedInfo;
	}
	
	public boolean willDispatchCommitted() {
		return server().caresAboutCommitted();
	}

	public ClassInfoHelper classInfoHelper() {
		return server().classInfoHelper();
	}

	/**
	 * EventArgs => MessageEventArgs
	 */
	public Event4<MessageEventArgs> messageReceived() {
		return _messageReceived;
    }
}