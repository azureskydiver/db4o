/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.staging;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.messaging.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class PingTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new PingTestCase().runAll();
	}

	protected void configure(Configuration config) {
		config.clientServer().timeoutClientSocket(1000);
	}

	TestMessageRecipient recipient = new TestMessageRecipient();

	public void test() {
		clientServerFixture().server().ext().configure().clientServer()
				.setMessageRecipient(recipient);

		final ExtObjectContainer client = clientServerFixture().db();
		final MessageSender sender = client.configure().clientServer()
				.getMessageSender();
		
		if(isMTOC()){
		    Assert.expect(NotSupportedException.class, new CodeBlock(){
                public void run() throws Throwable {
                    sender.send(new Data());
                }
		    });
		    return;
		}
		
	    sender.send(new Data());
		

		// The following query will be block by the sender
		ObjectSet os = client.get(null);
		while (os.hasNext()) {
			os.next();
		}
		Assert.isFalse(client.isClosed());
	}

	public static class TestMessageRecipient implements MessageRecipient {
		public void processMessage(MessageContext con, Object message) {
			Cool.sleepIgnoringInterruption(3000);
		}
	}

	public static class Data {
	}
}