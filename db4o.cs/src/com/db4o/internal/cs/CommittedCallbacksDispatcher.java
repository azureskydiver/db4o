/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.internal.cs;

import com.db4o.foundation.*;
import com.db4o.internal.cs.messages.*;

public class CommittedCallbacksDispatcher implements Runnable {
	
	private boolean _stopped;
	
	private final BlockingQueue _committedInfosQueue;
	
	private final ObjectServerImpl _server;
	
	public CommittedCallbacksDispatcher(ObjectServerImpl server, BlockingQueue committedInfosQueue) {
		_server = server;
		_committedInfosQueue = committedInfosQueue;
	}
	
	public void run () {
		while(! _stopped){
			MCommittedInfo committedInfos;
			try {
				committedInfos = (MCommittedInfo) _committedInfosQueue.next();
			} catch (BlockingQueueStoppedException e) {
				break;
			}
			_server.broadcastMsg(committedInfos, new BroadcastFilter() {
				public boolean accept(ServerMessageDispatcher dispatcher) {
					return dispatcher.caresAboutCommitted();
				}
			});
		}
	}
	
	public void stop(){
		_committedInfosQueue.stop();
		_stopped = true;
	}

}
