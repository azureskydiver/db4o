/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.messaging.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ClientTimeOutTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new ClientTimeOutTestCase().runAll();
	}

	protected void store() throws Exception {
		for (int i = 0; i < 1000; ++i)
			store(new Data());
	}

	protected void configure(Configuration config) {
		config.clientServer().timeoutClientSocket(1);
	}

	TestMessageRecipient recipient = new TestMessageRecipient();

	public void test() {
		clientServerFixture().server().ext().configure().clientServer()
				.setMessageRecipient(recipient);

		ExtObjectContainer client1 = clientServerFixture().db();
		MessageSender sender = client1.configure().clientServer()
				.getMessageSender();
		sender.send(new Data());
		
		// The following query will be block by the sender
		ObjectSet os = client1.get(null);
		while (os.hasNext()) {
			os.next();
		}
		Assert.isFalse(client1.isClosed());
	}

	public static class TestMessageRecipient implements MessageRecipient {
		public void processMessage(ObjectContainer con, Object message) {
			Cool.sleepIgnoringInterruption(3000);
		}
	}

	public static class Data {
	}
}