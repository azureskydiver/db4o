/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.messaging.*;

public final class MUserMessage extends MsgObject implements ServerSideMessage {
	
	public final boolean processAtServer() {
		if (messageRecipient() != null) {
			unmarshall();
			messageRecipient().processMessage(transaction().objectContainer(), readObjectFromPayLoad());
		}
		return true;
	}
	
	private MessageRecipient messageRecipient() {
		return config().messageRecipient();
	}
}