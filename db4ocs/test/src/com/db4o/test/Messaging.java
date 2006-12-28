/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectContainer;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.messaging.MessageRecipient;
import com.db4o.messaging.MessageSender;
import com.db4o.test.config.TestConfigure;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class Messaging extends ClientServerTestCase {

	static final String MSG = "hibabe";

	public String messageString;

	TestMessageRecipient recipient1 = new TestMessageRecipient();

	public void conc(ExtObjectContainer oc, int seq) {
		server().ext().configure().clientServer().setMessageRecipient(recipient1);
		MessageSender sender = oc.configure().clientServer().getMessageSender();
		this.messageString = MSG;
		// FIXME: it throws NPE sometimes
		sender.send(new Data(seq));
	}

	public void check(ExtObjectContainer oc) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
		recipient1.check();

	}

	
}

class TestMessageRecipient implements MessageRecipient {

	public int seq;

	public Object lastMessage;

	boolean processed[] = new boolean[TestConfigure.CONCURRENCY_THREAD_COUNT];

	public void processMessage(ObjectContainer con, Object message) {
		Assert.isTrue(message instanceof Data);
		int value = ((Data) message).value;
		processed[value] = true;
	}

	public void check() {
		for (int i = 0; i < TestConfigure.CONCURRENCY_THREAD_COUNT; ++i) {
			Assert.isTrue(processed[i]);
		}
	}
}

class Data {
	int value;

	public Data(int seq) {
		this.value = seq;
	}
}