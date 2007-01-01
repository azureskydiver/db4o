/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.messaging;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.messaging.*;


public class MessagingExample {
	public final static String YAPFILENAME="formula1.yap";
	
	public static void configureServer() {
		ObjectServer objectServer = Db4o.openServer(YAPFILENAME, 0);
		objectServer.ext().configure().clientServer().setMessageRecipient(
				new MessageRecipient() {
					public void processMessage(ObjectContainer objectContainer,
							Object message) {
						// message objects will arrive in this code block
						System.out.println(message);
					}
				});
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
