/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.io.*;
import com.db4o.messaging.*;

import db4ounit.*;

public class PrimitiveMessageTestCase implements TestCase {
	
	public static void main(String[] args) {
		new TestRunner(PrimitiveMessageTestCase.class).run();
	}
	
	public void testCustomMessageReply() {
		
		final Collection4 messages = new Collection4();
		final MessageRecipient recipient = new MessageRecipient() {
			public void processMessage(ObjectContainer container, Object message) {
				messages.add(message);
			}
		};
		
		final ObjectServer server = openServerWith(recipient);
		try {
			
			final ObjectContainer client = openClient(server);
			try {
				final MessageSender sender = messageSender(client);
				sender.send("PING");
			} finally {
				client.close();
			}
		} finally {
			server.close();
		}
		
		Assert.areEqual("[PING]", messages.toString());
	}

	private MessageSender messageSender(final ObjectContainer client) {
		return client.ext().configure().clientServer().getMessageSender();
	}

	private ObjectContainer openClient(final ObjectServer server) {
		return Db4o.openClient("127.0.0.1", server.ext().port(), "u", "p");
	}

	private ObjectServer openServerWith(final MessageRecipient recipient) {
		
		final Configuration config = Db4o.newConfiguration();
		config.io(new MemoryIoAdapter());
		config.clientServer().setMessageRecipient(recipient);
		
		final ObjectServer server = Db4o.openServer(config, "nofile", 0xdb40);
		server.grantAccess("u", "p");
		
		return server;
	}

}
