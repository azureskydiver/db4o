/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

public final class ServerMessageDispatcherImpl extends Thread implements ServerMessageDispatcher {

    private String i_clientName;

    private boolean i_loggedin;
    private long _lastActiveTime;

    private boolean i_sendCloseMessage = true;

    private final ObjectServerImpl i_server;

    private Socket4 i_socket;

    private ClientTransactionHandle _transactionHandle;
    
    private Hashtable4 _queryResults;
    
    private Config4Impl i_config;

    final int i_threadID;

	private CallbackObjectInfoCollections _committedInfo;

	private boolean _caresAboutCommitted;
	
	private boolean _isClosed;
	
	private final Object _lock = new Object();
	private final Object _mainLock;

    ServerMessageDispatcherImpl(ObjectServerImpl server,
			ClientTransactionHandle transactionHandle, Socket4 socket,
			int threadID, boolean loggedIn, Object mainLock) throws Exception {

    	_mainLock = mainLock;
		_transactionHandle = transactionHandle;

		setDaemon(true);

		i_loggedin = loggedIn;

		updateLastActiveTime();
		i_server = server;
		i_config = (Config4Impl) i_server.configure();
		i_threadID = threadID;
		setDispatcherName("" + threadID);
		i_socket = socket;
		i_socket.setSoTimeout(((Config4Impl) server.configure())
				.timeoutServerSocket());

		// TODO: Experiment with packetsize and noDelay
		// i_socket.setSendBufferSize(100);
		// i_socket.setTcpNoDelay(true);
	}

    public boolean close() {
    	synchronized(_mainLock) {
	    	synchronized(_lock) {
				if (!isMessageDispatcherAlive()) {
					return true;
				}
				_transactionHandle.releaseTransaction();
				sendCloseMessage();
				_transactionHandle.close();
				closeSocket();
				removeFromServer();
				_isClosed = true;
				return true;
	    	}
    	}
	}

    public void closeConnection() {
		synchronized (_mainLock) {
			synchronized (_lock) {
				if (!isMessageDispatcherAlive()) {
					return;
				}
				sendCloseMessage();
				closeSocket();
				removeFromServer();
				_isClosed = true;
			}
		}
	}
		
    
	public void sendCloseMessage() {
		try {
            if (i_sendCloseMessage) {
                write(Msg.CLOSE);
            }
            i_sendCloseMessage = false;
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }

        }
	}

	private void removeFromServer() {
		try {
            i_server.removeThread(this);
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
	}

	private void closeSocket() {
		try {
			if(i_socket != null) {
				i_socket.close();
			}
        } catch (Db4oIOException e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
	}

    public synchronized boolean isMessageDispatcherAlive() {
		return !_isClosed;
	}

	public Transaction getTransaction() {
    	return _transactionHandle.transaction();
    }

    public void run() {
        while (isMessageDispatcherAlive()) {
            try {
                if(! messageProcessor()){
                    break;
                }
            } catch (Db4oIOException e) {
                if (Debug.atHome) {
                    e.printStackTrace();
                }
                if (_transactionHandle.isClosed()) {
                    break;
                }
                if(i_socket == null ||  ! i_socket.isConnected()){
                	break;
                }
            }
        }
        close();
    }

    private boolean messageProcessor() throws Db4oIOException{
        Msg message = Msg.readMessage(this, getTransaction(), i_socket);
        if(message == null){
            return true;
        }
        updateLastActiveTime();
        if(!i_loggedin && !Msg.LOGIN.equals(message)) {
        	return true;
        }

        // TODO: COR-885 - message may process against closed server
        // Checking aliveness just makes the issue less likely to occur. Naive synchronization against main lock is prohibitive.        
    	if(isMessageDispatcherAlive()) {
    		return ((ServerSideMessage)message).processAtServer();
    	}
    	return false;
    }

	private void updateLastActiveTime() {
		_lastActiveTime = System.currentTimeMillis();
	}

    public ObjectServerImpl server() {
    	return i_server;
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
                if (Debug.atHome) {
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
		Transaction transToUse = i_server.findTransaction(threadID);
		_transactionHandle.transaction(transToUse);
    }
    
    public void write(Msg msg){
    	synchronized(_lock) {
    		msg.write(i_socket);
	    	updateLastActiveTime();
    	}
    }
    
    public void writeIfAlive(Msg msg){
    	synchronized(_lock) {
	    	if(isMessageDispatcherAlive()){
	        	write(msg);
	    	}
    	}
    }
    
    public Socket4 socket(){
    	return i_socket;
    }

	public String name() {
		return i_clientName;
	}
	
	public void setDispatcherName(String name) {
		i_clientName = name;
		// set thread name
		setName("db4o server message dispatcher " + name);
	}
    
    public int dispatcherID() {
    	return i_threadID;
    }

	public void login() {
		i_loggedin = true;
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

	public void committedInfo(CallbackObjectInfoCollections committedInfo) {
		_committedInfo = committedInfo;
	}

	public boolean isPingTimeout() {
		long elapsed = System.currentTimeMillis() - _lastActiveTime;
		return i_loggedin && ((elapsed > i_config.pingInterval()));
	}

}