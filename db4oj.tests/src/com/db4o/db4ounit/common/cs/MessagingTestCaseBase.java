/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.io.*;
import com.db4o.messaging.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class MessagingTestCaseBase implements TestCase, OptOutCS {
	
	public static final class MessageCollector implements MessageRecipient {
		public final Collection4 messages = new Collection4();
		
		public void processMessage(ObjectContainer container, Object message) {
			messages.add(message);
		}
	}

	protected MessageSender messageSender(final ObjectContainer client) {
		return client.ext().configure().clientServer().getMessageSender();
	}

	protected ObjectContainer openClient(String clientId, final ObjectServer server) {
		server.grantAccess(clientId, "p");
		return Db4o.openClient("127.0.0.1", server.ext().port(), clientId, "p");
	}

	protected ObjectServer openServerWith(final MessageRecipient recipient) {
		
		final Configuration config = Db4o.newConfiguration();
		config.io(new MemoryIoAdapter());
		config.clientServer().setMessageRecipient(recipient);
		
		return Db4o.openServer(config, "nofile", 0xdb40);
	}

}