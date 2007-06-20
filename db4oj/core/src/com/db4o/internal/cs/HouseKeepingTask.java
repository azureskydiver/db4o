/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.internal.cs;

import com.db4o.internal.cs.messages.*;

public class HouseKeepingTask implements Runnable {

	private final ObjectServerImpl _server;

	public HouseKeepingTask(ObjectServerImpl server) {
		_server = server;
	}

	public void run() {
		broadcastPing();
	}

	private void broadcastPing() {
		_server.broadcastMsg(Msg.PING, new BroadcastFilter() {
			public boolean accept(ServerMessageDispatcher dispatcher) {
				return dispatcher.isPingTimeout();
			}
		});
	}

}
