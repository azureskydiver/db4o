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

	protected void configure(Configuration config) {
		config.clientServer().timeoutClientSocket(1000);
	}

	TestMessageRecipient recipient = new TestMessageRecipient();

	public void test() {
       if(isMTOC()){
            // This test really doesn't make sense for MTOC, there
            // is no client to time out.
            return;
        }

		clientServerFixture().server().ext().configure().clientServer()
				.setMessageRecipient(recipient);

		final ExtObjectContainer client = clientServerFixture().db();
		MessageSender sender = client.configure().clientServer()
				.getMessageSender();
		sender.send(new Data());
		
		// The following query will be blocked by the sender
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				ObjectSet os = client.get(null);
				while (os.hasNext()) {
					os.next();
				}
			}
		});
	}

	public static class TestMessageRecipient implements MessageRecipient {
		public void processMessage(ObjectContainer con, Object message) {
			Cool.sleepIgnoringInterruption(3000);
		}
	}

	public static class Data {
	}
}