/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.cs.*;
import com.db4o.internal.cs.messages.*;

import db4ounit.*;

/**
 * @exclude
 */
public class ClientServerPingTestCase extends ClientServerTestCaseBase {

	private static final int	ITEM_COUNT	= 100;

	public static void main(String[] arguments) {
		new ClientServerPingTestCase().runClientServer();
	}

	protected void configure(Configuration config) {
		config.clientServer().batchMessages(false);
	}

	public void test() throws Exception {
		ServerMessageDispatcher dispatcher = serverDispatcher();
		PingThread pingThread = new PingThread(dispatcher);
		pingThread.start();
		for (int i = 0; i < ITEM_COUNT; i++) {
			Item item = new Item(i);
			store(item);
		}
		Assert.areEqual(ITEM_COUNT, db().get(Item.class).size());
		pingThread.close();
	}

	public static class Item {

		public int	data;

		public Item(int i) {
			data = i;
		}

	}

	static class PingThread extends Thread {

		ServerMessageDispatcher	_dispatcher;
		boolean					_stop;

		public PingThread(ServerMessageDispatcher dispatcher) {
			_dispatcher = dispatcher;
		}

		public void close() {
			_stop = true;
		}

		public void run() {
			while (!_stop) {
				_dispatcher.write(Msg.PING);
				Cool.sleepIgnoringInterruption(1);
			}
		}
	}

}
