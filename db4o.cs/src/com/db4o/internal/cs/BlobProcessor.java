/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.cs.messages.*;

class BlobProcessor extends Thread{
	
	private ClientObjectContainer			stream;
	private Queue4 				queue = new NonblockingQueue();
	private boolean				terminated = false;
	
	BlobProcessor(ClientObjectContainer aStream){
		stream = aStream;
		adjustThreadPriority();
	}

	/**
	 * @sharpen.remove
	 */
	private void adjustThreadPriority() {
	    setPriority(MIN_PRIORITY);
    }

	void add(MsgBlob msg){
		synchronized(queue){
			queue.add(msg);
		}
	}
	
	synchronized boolean isTerminated(){
		return terminated;
	}
	
	public void run(){
		try{
			Socket4 socket = stream.createParalellSocket();
			
			MsgBlob msg = null;
			
			// no blobLock synchronisation here, since our first msg is valid
			synchronized(queue){
				msg = (MsgBlob)queue.next();
			}
			
			while(msg != null){
				msg.write(socket);
				msg.processClient(socket);
				synchronized(stream.blobLock){
					synchronized(queue){
						msg = (MsgBlob)queue.next();
					}
					if(msg == null){
						terminated = true;
						Msg.CLOSE_SOCKET.write(socket);
						try{
							socket.close();
						}catch(Exception e){
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
