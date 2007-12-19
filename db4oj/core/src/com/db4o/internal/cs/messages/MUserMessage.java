/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.messaging.*;

public final class MUserMessage extends MsgObject implements ServerSideMessage {
	
	public final boolean processAtServer() {
		if (messageRecipient() != null) {
			unmarshall();
			try {
				final UserMessagePayload payload = (UserMessagePayload)readObjectFromPayLoad();
				messageRecipient().processMessage(transaction().objectContainer(), payload.message);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return true;
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