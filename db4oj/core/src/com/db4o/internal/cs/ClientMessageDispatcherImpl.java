/* Copyright (C) 2004 - 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

class ClientMessageDispatcherImpl extends Thread implements ClientMessageDispatcher {
	
	private ClientObjectContainer _container;
	private Socket4 _socket;
	private final BlockingQueue _synchronousMessageQueue;
	private final BlockingQueue _asynchronousMessageQueue;
	private boolean _isClosed;
	
	ClientMessageDispatcherImpl(
			ClientObjectContainer client, 
			Socket4 a_socket, 
			BlockingQueue synchronousMessageQueue, 
			BlockingQueue asynchronousMessageQueue){
		_container = client;
		_synchronousMessageQueue = synchronousMessageQueue;
		_asynchronousMessageQueue = asynchronousMessageQueue;
		_socket = a_socket;
	}
	
	public synchronized boolean isMessageDispatcherAlive() {
		return !_isClosed;
	}

	public synchronized boolean close() {
	    if(_isClosed){
	        return true;
	    }
		_isClosed = true;
		if(_socket != null) {
			try {
				_socket.close();
			} catch (Db4oIOException e) {
				
			}
		}
		_synchronousMessageQueue.stop();
		_asynchronousMessageQueue.stop();
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
				message = Msg.readMessage(this, transaction(), _socket);
			} catch (Db4oIOException exc) {
				if(DTrace.enabled){
					DTrace.CLIENT_MESSAGE_LOOP_EXCEPTION.log(exc.toString());
				}
			    return;
            }
			if(message == null){
			    continue;
			}
			if (isClientSideMessage(message)) {
				_asynchronousMessageQueue.add(message);
			} else {
				_synchronousMessageQueue.add(message);
			}
		}
	}
	
	private boolean isClientSideMessage(Msg message) {
		return message instanceof ClientSideMessage;
	}
	
	public boolean write(Msg msg) {
		_container.write(msg);
		return true;
	}

	public void setDispatcherName(String name) {
		setName("db4o client side message dispatcher for " + name);
	}

	public void startDispatcher() {
		start();
	}
	
	private Transaction transaction(){
	    return _container.transaction();
	}
	
}
