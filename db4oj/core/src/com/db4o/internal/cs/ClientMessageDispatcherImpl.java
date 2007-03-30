/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.cs.messages.*;

class ClientMessageDispatcherImpl extends Thread implements ClientMessageDispatcher {
	
	private ClientObjectContainer i_stream;
	private Socket4 i_socket;
	private final Queue4 _messageQueue;
	
	ClientMessageDispatcherImpl(ClientObjectContainer client, Socket4 a_socket, Queue4 messageQueue_){
		i_stream = client;
		_messageQueue = messageQueue_;
		i_socket = a_socket;
	}
	
	public synchronized boolean isMessageDispatcherAlive(){
		return i_socket != null;
	}
	
	public synchronized boolean close(){
		i_stream = null;
		i_socket = null;
		return true;
	}
	
	public void run() {
		while (isMessageDispatcherAlive()) {
			Msg message = null;
			try {
				message = Msg.readMessage(this, i_stream.getTransaction(),
						i_socket);
				if (message instanceof ClientSideMessage) {
					if (((ClientSideMessage) message).processAtClient()) {
						continue;
					}
				}
			} catch (IOException exc) {
				close();
				message = Msg.ERROR;
			}
			_messageQueue.add(message);
		}
	}
	
	public void write(Msg msg) {
		i_stream.write(msg);
	}

	public void setDispatcherName(String name) {
		setName("db4o message client for user " + name);
	}

	public void startDispatcher() {
		start();
	}
	
}
