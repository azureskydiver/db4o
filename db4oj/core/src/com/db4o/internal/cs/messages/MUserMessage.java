/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.messaging.*;
import com.db4o.messaging.internal.*;

public final class MUserMessage extends MsgObject implements ServerSideMessage, ClientSideTask, MessageContextProvider {
	
	public final boolean processAtServer() {
		return processUserMessage();
	}
	
	public boolean runOnClient() {
		return processUserMessage();
	}
	
	private boolean processUserMessage() {
		final MessageRecipient recipient = messageRecipient();
		if (recipient == null) {
			return true;
		}
		
		try {
			MessageContextInfrastructure.contextProvider.with(this, new Runnable() {
				public void run() {
					recipient.processMessage(transaction().objectContainer(), readUserMessage());
				}
			});
			
		} catch (Exception x) {
			// TODO: use MessageContext.sender() to send
			// error back to client
			x.printStackTrace();
		}
		return true;
	}
	
	public MessageContext messageContext() {
		return new MessageContext() {
			public MessageSender sender() {
				return new MessageSender() {
					public void send(Object message) {
						serverMessageDispatcher().write(Msg.USER_MESSAGE.marshallUserMessage(transaction(), message));
					}
				};
			}
		};
	}

	private Object readUserMessage() {
		unmarshall();
		return ((UserMessagePayload)readObjectFromPayLoad()).message;
	}
	
	private MessageRecipient messageRecipient() {
		return config().messageRecipient();
	}
	
	public static final class UserMessagePayload {
		public Object message;
		
		public UserMessagePayload() {
		}
		
		public UserMessagePayload(Object message_) {
			message = message_;
		}
	}

	public Msg marshallUserMessage(Transaction transaction, Object message) {
		return getWriter(Serializer.marshall(transaction, new UserMessagePayload(message)));
	}
}