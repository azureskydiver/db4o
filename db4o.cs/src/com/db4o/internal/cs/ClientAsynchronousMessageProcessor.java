/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.*;
import com.db4o.internal.cs.messages.*;

/**
 * @exclude
 */
public class ClientAsynchronousMessageProcessor extends Thread {
	
	private final BlockingQueue _messageQueue;
	
	private boolean _stopped;
	
	public ClientAsynchronousMessageProcessor(BlockingQueue messageQueue){
		_messageQueue = messageQueue;
	}
	
	public void run() {
		while(! _stopped){
			try {
				ClientSideMessage message = (ClientSideMessage) _messageQueue.next();
				if(message != null){
					message.processAtClient();
				}
			} catch (BlockingQueueStoppedException e) {
				break;
			}
		}
	}
	
	public void startProcessing(){
		start();
	}
	
	public void stopProcessing(){
		_stopped = true;
	}
	
	
	

}
