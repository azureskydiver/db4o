/* Copyright (C) 2004 - 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

class ClientMessageDispatcherImpl extends Thread implements ClientMessageDispatcher {
	
	private ClientObjectContainer i_stream;
	private Socket4 i_socket;
	private final BlockingQueue _messageQueue;
	private boolean _isClosed;
	
	ClientMessageDispatcherImpl(ClientObjectContainer client, Socket4 a_socket, BlockingQueue messageQueue_){
		i_stream = client;
		_messageQueue = messageQueue_;
		i_socket = a_socket;
	}
	
	public synchronized boolean isMessageDispatcherAlive() {
		return !_isClosed;
	}

	public synchronized boolean close() {
	    if(_isClosed){
	        return true;
	    }
		_isClosed = true;
		if(i_socket != null) {
			try {
				i_socket.close();
			} catch (Db4oIOException e) {
				
			}
		}
		_messageQueue.stop();
		return true;
	}
	
	public void run() {
	    messageLoop();
	    close();
	}
	
	public void messageLoop() {
		while (isMessageDispatcherAlive()) {
			Msg message = null;
			try {
				message = Msg.readMessage(this, transaction(), i_socket);
			} catch (Db4oIOException exc) {
				if(DTrace.enabled){
					DTrace.CLIENT_MESSAGE_LOOP_EXCEPTION.log(exc.toString());
				}
			    return;
            }
			if(message == null){
			    continue;
			}
			
			// TODO are there possibly messages that have to be processed *and* passed on?
			if (isClientSideMessage(message)) {
				if (((ClientSideMessage) message).processAtClient()) {
					continue;
				}
			}
			_messageQueue.add(message);
			
		}
	}
	
	private boolean isClientSideMessage(Msg message) {
		return message instanceof ClientSideMessage;
	}
	
	public boolean write(Msg msg) {
		i_stream.write(msg);
		return true;
	}

	public void setDispatcherName(String name) {
		setName("db4o client side message dispather for " + name);
	}

	public void startDispatcher() {
		start();
	}
	
	private Transaction transaction(){
	    return i_stream.transaction();
	}
	
}
