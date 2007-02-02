/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;
import com.db4o.messaging.*;

public final class MUserMessage extends MsgObject {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		if (messageRecipient() != null) {
			unmarshall();
			messageRecipient().processMessage(stream(), stream().unmarshall(_payLoad));
		}
		return true;
	}
	
	private MessageRecipient messageRecipient() {
		return config().messageRecipient();
	}
}