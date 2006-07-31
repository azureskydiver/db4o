/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.IOException;

import com.db4o.foundation.network.*;

final class YapServerThread extends Thread {

    private String i_clientName;

    private boolean i_loggedin;
    private long i_lastClientMessage;
    private final YapFile i_mainStream;

    private Transaction i_mainTrans;
    private int i_pingAttempts = 0;
    private int i_nullMessages;
    private boolean i_rollbackOnClose = true;
    private boolean i_sendCloseMessage = true;

    private final YapServer i_server;

    private YapSocket i_socket;
    private YapFile i_substituteStream;
    private Transaction i_substituteTrans;
    
    private Config4Impl i_config;

    final int i_threadID;

    YapServerThread(
        YapServer aServer,
        YapFile aStream,
        YapSocket aSocket,
        int aThreadID,
        boolean loggedIn)
        throws Exception {
        
        	
        i_loggedin = loggedIn;
         
        i_lastClientMessage = System.currentTimeMillis(); // don't start pinging from the start
        i_server = aServer;
		i_config = (Config4Impl)i_server.configure();
        i_mainStream = aStream;
        i_threadID = aThreadID;
        setName("db4o message server " + aThreadID);
        i_mainTrans = new Transaction(aStream, aStream.getSystemTransaction());
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

    public void close() {
        closeSubstituteStream();
        try {
            if (i_sendCloseMessage) {
                Msg.CLOSE.write(i_mainStream, i_socket);
            }
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }

        }
        if (i_mainStream != null && i_mainTrans != null) {
            i_mainTrans.close(i_rollbackOnClose);
        }
        try {
            i_socket.close();
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
        i_socket = null;
        try {
            i_server.removeThread(this);
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
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

    private final YapFile getStream() {
        if (i_substituteStream != null) {
            return i_substituteStream;
        }
        return i_mainStream;
    }

    Transaction getTransaction() {
        if (i_substituteTrans != null) {
            return i_substituteTrans;
        }
        return i_mainTrans;
    }

    public void run() {
        while (i_socket != null) {
            try {
                if(! messageProcessor()){
                    break;
                }
            } catch (Exception e) {
                if (i_mainStream == null || i_mainStream.isClosed()) {
                    break;
                }
                if(! i_socket.isConnected()){
                    break;
                }
                if (Deploy.debug) {
                    e.printStackTrace();
                }
                i_nullMessages++;
            }
            
            // TODO: Optimize - this doesn't need to be in the loop of executing statements

            if (i_nullMessages > 20 || pingClientTimeoutReached()) {
                if (i_pingAttempts > 5) {
                    // 
                    getStream().logMsg(33, i_clientName);
                    break;
                }
                if (null == i_socket) break;
                Msg.PING.write(getStream(), i_socket);
                i_pingAttempts++;
            }
        }
        close();
    }

	private boolean pingClientTimeoutReached() {
		return (System.currentTimeMillis() - i_lastClientMessage > i_config.timeoutPingClients());
	}
    
    private boolean messageProcessor() throws IOException{
        
        Msg message = Msg.readMessage(getTransaction(), i_socket);
        if(message == null){
            i_nullMessages ++;
            return true;
        }
        
        i_lastClientMessage = System.currentTimeMillis();
        i_nullMessages = 0;
        i_pingAttempts = 0;
        if (! i_loggedin) {
            if (Msg.LOGIN.equals(message)) {
                String userName = ((MsgD) message).readString();
                String password = ((MsgD) message).readString();
                User user = new User();
                user.name = userName;
                i_mainStream.showInternalClasses(true);
                User found = (User) i_mainStream.get(user).next();
                i_mainStream.showInternalClasses(false);
                if (found != null) {
                    if (found.password.equals(password)) {
                        i_clientName = userName;
                        i_mainStream.logMsg(32, i_clientName);
                        int blockSize = i_mainStream.blockSize();
                        int encrypt = i_mainStream.i_handlers.i_encrypt ? 1 : 0;
                        Msg.LOGIN_OK.getWriterForInts(getTransaction(), new int[] {blockSize, encrypt}).write(i_mainStream, i_socket);
                        i_loggedin= true;
                        setName("db4o server socket for client " + i_clientName);
                    } else {
                        Msg.FAILED.write(i_mainStream, i_socket);
                        return false;
                    }
                } else {
                    Msg.FAILED.write(i_mainStream, i_socket);
                    return false;
                }
            }
            return true;
        }
        
        if (message.processMessageAtServer(i_socket)) {
            return true;
        }
        
        if (Msg.PING.equals(message)) {
            Msg.OK.write(getStream(), i_socket);
            return true;
        }
        if (Msg.CLOSE.equals(message)) {
            Msg.CLOSE.write(getStream(), i_socket);
            getTransaction().commit();
            i_sendCloseMessage = false;
            getStream().logMsg(34, i_clientName);
            return false;
        }
        
        if (Msg.IDENTITY.equals(message)) {
            respondInt((int)getStream().getID(getStream().bootRecord().i_db));
            return true;
        }
        
        if (Msg.CURRENT_VERSION.equals(message)){
            long ver = 0;
            synchronized(getStream()){
                ver = getStream().currentVersion();
            }
            Msg.ID_LIST.getWriterForLong(getTransaction(), ver).write(getStream(), i_socket);
            return true;
        }
        
        if (Msg.RAISE_VERSION.equals(message)) {
            long minimumVersion = ((MsgD)message).readLong();
            YapStream stream = getStream();
            synchronized(stream){
                stream.raiseVersion(minimumVersion);
            }
            return true;
        } 
        
        if (Msg.GET_THREAD_ID.equals(message)) {
            respondInt(i_threadID);
            return true;
        }
        
        if (Msg.SWITCH_TO_FILE.equals(message)) {
            switchToFile(message);
            return true;
        }
        
        if (Msg.SWITCH_TO_MAIN_FILE.equals(message)) {
            switchToMainFile();
            return true;
        }
        
        if (Msg.USE_TRANSACTION.equals(message)) {
            useTransaction(message);
            return true;
        }
        
        return true;
    }
    
    

    private void switchToFile(Msg message) {
        synchronized (i_mainStream.i_lock) {
            String fileName = ((MsgD) message).readString();
            try {
                closeSubstituteStream();
                i_substituteStream = (YapFile) Db4o.openFile(fileName);
                i_substituteTrans =
                    new Transaction(i_substituteStream, i_substituteStream.getSystemTransaction());
                i_substituteStream.i_config.setMessageRecipient(i_mainStream.i_config.messageRecipient());
                Msg.OK.write(getStream(), i_socket);
            } catch (Exception e) {
                if (Debug.atHome) {
                    System.out.println("Msg.SWITCH_TO_FILE failed.");
                    e.printStackTrace();
                }
                closeSubstituteStream();
                Msg.ERROR.write(getStream(), i_socket);
            }
        }
    }

    private void switchToMainFile() {
        synchronized (i_mainStream.i_lock) {
            closeSubstituteStream();
            Msg.OK.write(getStream(), i_socket);
        }
    }

    private void useTransaction(Msg message) {
        int threadID = ((MsgD) message).readInt();
        YapServerThread transactionThread = i_server.findThread(threadID);
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
    
    private void respondInt(int response){
        Msg.ID_LIST.getWriterForInt(getTransaction(), response).write(getStream(),i_socket);
    }
    
    
}