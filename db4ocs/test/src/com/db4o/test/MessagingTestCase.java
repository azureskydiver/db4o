/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.messaging.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class MessagingTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new MessagingTestCase().runConcurrency();
	}

	TestMessageRecipient recipient = new TestMessageRecipient();

	public void conc(ExtObjectContainer oc, int seq) {
		clientServerFixture().server().ext().configure().clientServer()
				.setMessageRecipient(recipient);
		MessageSender sender = oc.configure().clientServer().getMessageSender();
		sender.send(new Data(seq));
	}

	public void check(ExtObjectContainer oc) throws Exception {
		Thread.sleep(1000);
		recipient.check();
	}

	static class TestMessageRecipient implements MessageRecipient {
		public int seq;
		
		private boolean [] processed = new boolean [threadCount()];
		
		public void processMessage(ObjectContainer con, Object message) {
			Assert.isTrue(message instanceof Data);
			int value = ((Data) message).value;
			processed[value] = true;
		}

		public void check() {
			for (int i = 0; i < processed.length; ++i) {
				Assert.isTrue(processed[i]);
			}
		}
	}

	static class Data {
		int value;

		public Data(int seq) {
			this.value = seq;
		}
	}
}