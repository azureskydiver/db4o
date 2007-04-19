/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

public final class ServerMessageDispatcherImpl extends Thread implements ServerMessageDispatcher {

    private String i_clientName;

    private boolean i_loggedin;
    private long _lastClientMessageTime;
    private final LocalObjectContainer i_mainStream;

    private Transaction i_mainTrans;
    private int i_pingAttempts = 0;
    private boolean i_rollbackOnClose = true;
    private boolean i_sendCloseMessage = true;

    private final ObjectServerImpl i_server;

    private Socket4 i_socket;
    private LocalObjectContainer i_substituteStream;
    private Transaction i_substituteTrans;
    
    private Hashtable4 _queryResults;
    
    private Config4Impl i_config;

    final int i_threadID;

	private CallbackObjectInfoCollections _committedInfo;

	private boolean _caresAboutCommitted;
    

    ServerMessageDispatcherImpl(
        ObjectServerImpl aServer,
        LocalObjectContainer aStream,
        Socket4 aSocket,
        int aThreadID,
        boolean loggedIn)
        throws Exception {
    	
    	setDaemon(true);
        	
        i_loggedin = loggedIn;
         
        _lastClientMessageTime = System.currentTimeMillis(); // don't start pinging from the start
        i_server = aServer;
		i_config = (Config4Impl)i_server.configure();
        i_mainStream = aStream;
        i_threadID = aThreadID;
        setDispatcherName("db4o message server " + aThreadID);
        i_mainTrans = aStream.newTransaction();
        try {
            i_socket = aSocket;
            i_socket.setSoTimeout(((Config4Impl)aServer.configure()).timeoutServerSocket());

            // TODO: Experiment with packetsize and noDelay
            // i_socket.setSendBufferSize(100);
            // i_socket.setTcpNoDelay(true);

        } catch (Exception e) {
            i_socket.close();
            throw (e);
        }
    }

    public synchronized boolean close() {
    	if (!isMessageDispatcherAlive()) { 
    		return true;
    	}    	
        closeSubstituteStream();
        sendCloseMessage();
        rollbackMainTransaction();
        closeSocket();
        removeFromServer();
        return true;
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

	private void rollbackMainTransaction() {
		if (i_mainStream != null && i_mainTrans != null) {
            i_mainTrans.close(i_rollbackOnClose);
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
            i_socket.close();
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
        i_socket = null;
	}

    public boolean isMessageDispatcherAlive() {
		return i_socket != null;
	}

	private void closeSubstituteStream() {
        if (i_substituteStream != null) {
            if (i_substituteTrans != null) {
                i_substituteTrans.close(i_rollbackOnClose);
                i_substituteTrans = null;
            }
            try {
                i_substituteStream.close();

            } catch (Exception e) {
                if (Debug.atHome) {
                    e.printStackTrace();
                }
            }
            i_substituteStream = null;
        }
    }

    private final LocalObjectContainer getStream() {
        if (i_substituteStream != null) {
            return i_substituteStream;
        }
        return i_mainStream;
    }

    public Transaction getTransaction() {
        if (i_substituteTrans != null) {
            return i_substituteTrans;
        }
        return i_mainTrans;
    }

    public void run() {
        while (isMessageDispatcherAlive()) {
            try {
                if(! messageProcessor()){
                    break;
                }
            } catch (Exception e) {
                if (Debug.atHome) {
                    e.printStackTrace();
                }
                if (i_mainStream == null || i_mainStream.isClosed()) {
                    break;
                }
                if(i_socket == null ||  ! i_socket.isConnected()){
                	break;
                }
            }
            
            if (pingClientTimeoutReached()) {
                if (i_pingAttempts > 5) {
                    getStream().logMsg(33, i_clientName);
                    break;
                }
                if (isMessageDispatcherAlive()) {
					write(Msg.PING);
					i_pingAttempts++;
				}
            }
        }
        close();
    }

	private boolean pingClientTimeoutReached() {
		return (System.currentTimeMillis() - _lastClientMessageTime > i_config.timeoutPingClients());
	}

    
    private boolean messageProcessor() throws IOException{
        
        Msg message = Msg.readMessage(this, getTransaction(), i_socket);
        if(message == null){
            return true;
        }
        
        _lastClientMessageTime = System.currentTimeMillis();
        i_pingAttempts = 0;
        if(!i_loggedin && !Msg.LOGIN.equals(message)) {
        	return true;
        }
        return ((ServerSideMessage)message).processAtServer();
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
        synchronized (i_mainStream.i_lock) {
            String fileName = message.readString();
            try {
                closeSubstituteStream();
                i_substituteStream = (LocalObjectContainer) Db4o.openFile(fileName);
                i_substituteTrans = i_substituteStream.newTransaction();
                i_substituteStream.configImpl().setMessageRecipient(i_mainStream.configImpl().messageRecipient());
                write(Msg.OK);
            } catch (Exception e) {
                if (Debug.atHome) {
                    System.out.println("Msg.SWITCH_TO_FILE failed.");
                    e.printStackTrace();
                }
                closeSubstituteStream();
                write(Msg.ERROR);
            }
        }
    }

    public void switchToMainFile() {
        synchronized (i_mainStream.i_lock) {
            closeSubstituteStream();
            write(Msg.OK);
        }
    }

    public void useTransaction(MUseTransaction message) {
        int threadID = message.readInt();
        ServerMessageDispatcherImpl transactionThread = i_server.findThread(threadID);
        if (transactionThread != null) {
            Transaction transToUse = transactionThread.getTransaction();
            if (i_substituteTrans != null) {
                i_substituteTrans = transToUse;
            } else {
                i_mainTrans = transToUse;
            }
            i_rollbackOnClose = false;
        }
    }
    
    public void write(Msg msg){
    	msg.write(getStream(), i_socket);
    }
    
    public synchronized void writeIfAlive(Msg msg){
    	if(isMessageDispatcherAlive()){
        	write(msg);
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
		setName("db4o server socket for client " + name);
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
	}

	public CallbackObjectInfoCollections committedInfo() {
		return _committedInfo;
	}

	public void committedInfo(CallbackObjectInfoCollections committedInfo) {
		_committedInfo = committedInfo;
	}

}