/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.foundation.network.*;

class YapClientThread extends Thread{
	
	private Thread streamThread;
	private YapClient i_stream;
	private YapSocket i_socket;
	final Queue4 messageQueue;
	final Lock4 messageQueueLock;
	
	
	YapClientThread(YapClient client, YapSocket a_socket, Queue4 messageQueue, Lock4 messageQueueLock){
		synchronized(this){
			i_stream = client;
			this.messageQueue = messageQueue;
			i_socket = a_socket;
			streamThread = Thread.currentThread();
			this.messageQueueLock = messageQueueLock;
		}
	}
	
	synchronized boolean isClosed(){
		return i_socket == null;
	}
	
	synchronized void close(){
		i_stream = null;
		i_socket = null;
	}
	
	public void run() {
		while(i_socket != null){
			try {
                if(i_stream == null){
                    return;
                }
				final Msg message = Msg.readMessage(i_stream.getTransaction(), i_socket);
                if(i_stream == null){
                    return;
                }
				if(Msg.PING.equals(message)){
				    i_stream.writeMsg(Msg.OK);
				}else if(Msg.CLOSE.equals(message)){
					i_stream.logMsg(35, i_stream.toString());
                    if(i_stream == null){
                        return;
                    }
                    
                    // TODO: There was a strange notify call here,
                    // possibly to accelerate shutting down.
                    
                    // Old code was: i_stream.notify(), but we found
                    // no reference to YapStream.wait().
                    
                    // The possible intention was probably the following:
                    
                    // messageQueueLock.awake();
                    
                    i_stream = null;
                    i_socket = null;
				}else if (message != null){
					messageQueueLock.run(new Closure4() {
                        public Object run() {
							messageQueue.add(message);
							messageQueueLock.awake();
                            return null;
                        }
                    });
				}
			} catch (Exception e) {
			}
		}
	}
}
