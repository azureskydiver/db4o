/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

import com.db4o.cs.messages.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;

class YapClientThread extends Thread{
	
	private ClientObjectContainer i_stream;
	private YapSocket i_socket;
	final Queue4 messageQueue;
	final Lock4 messageQueueLock;
	
	
	YapClientThread(ClientObjectContainer client, YapSocket a_socket, Queue4 messageQueue_, Lock4 messageQueueLock_){
		synchronized(this){
			i_stream = client;
			messageQueue = messageQueue_;
			i_socket = a_socket;
			messageQueueLock = messageQueueLock_;
		}
	}
	
	synchronized boolean isClosed(){
		return i_socket == null;
	}
	
	synchronized void close(){
		i_stream = null;
		i_socket = null;
		//interrupt();
	}
	
	public void run() {
		while(i_socket != null){
			try {
                if(i_stream == null){
                    return;
                }
				final Msg message;
				try {
					message=Msg.readMessage(i_stream.getTransaction(), i_socket);
				}
				catch(Exception exc) {
					messageQueueLock.run(new Closure4() {
                        public Object run() {
							messageQueue.add(Msg.ERROR);
							close();
							messageQueueLock.awake();
                            return null;
                        }
                    });
					
					close();
					return;
				}
				if(Msg.PING.equals(message)){
				    i_stream.writeMsg(Msg.OK);
				}else if(Msg.CLOSE.equals(message)){
					i_stream.logMsg(35, i_stream.toString());
                    
                    // TODO: There was a strange notify call here,
                    // possibly to accelerate shutting down.
                    
                    // Old code was: i_stream.notify(), but we found
                    // no reference to YapStream.wait().
                    
                    // The possible intention was probably the following:
                    
                    // messageQueueLock.awake();
                    
                    i_stream = null;
                    i_socket = null;
				}else /*if (message != null)*/{
					messageQueueLock.run(new Closure4() {
                        public Object run() {
							messageQueue.add(message);
							messageQueueLock.awake();
                            return null;
                        }
                    });
				}
			} catch (Exception exc) {
				close();
				return;
			}
		}
	}
}
