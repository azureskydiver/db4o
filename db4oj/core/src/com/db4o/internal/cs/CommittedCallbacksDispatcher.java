/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.internal.cs;

import com.db4o.foundation.*;
import com.db4o.internal.*;

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
			CallbackObjectInfoCollections committedInfos =  (CallbackObjectInfoCollections) _committedInfosQueue.next();
			_server.sendCommittedInfo(committedInfos);
			Cool.sleepIgnoringInterruption(1);
		}
	}
	
	public void stop(){
		_stopped = true;
	}

}
