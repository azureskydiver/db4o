/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.concurrency;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.messaging.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class MessagingTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new MessagingTestCase().runConcurrency();
	}

	public TestMessageRecipient recipient = new TestMessageRecipient(threadCount());

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

	public static class TestMessageRecipient implements MessageRecipient {
		public int seq;

		public boolean[] processed;

		public TestMessageRecipient(int threadCount) {
			processed = new boolean[threadCount];
		}

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

	public static class Data {
		public int value;

		public Data(int seq) {
			this.value = seq;
		}
	}
}