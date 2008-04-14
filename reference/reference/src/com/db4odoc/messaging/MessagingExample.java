/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.messaging;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.messaging.*;


public class MessagingExample {
	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void configureServer() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.clientServer().setMessageRecipient(new MessageRecipient() {
			public void processMessage(MessageContext context,
					Object message) {
				// message objects will arrive in this code block
				System.out.println(message);
			}
		});
		ObjectServer objectServer = Db4o.openServer(configuration, DB4O_FILE_NAME, 0);
				
		try {
			ObjectContainer clientObjectContainer = objectServer.openClient();
			// Here is what we would do on the client to send the message
			MessageSender sender = clientObjectContainer.ext().configure().clientServer().getMessageSender();

			sender.send(new MyClientServerMessage("Hello from client."));
			clientObjectContainer.close();
		} finally {
			objectServer.close();
		}
	}
	// end configureServer 
	 
}
