/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;

public final class MUserMessage extends MsgObject {
	public final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		if (stream.configImpl().messageRecipient() != null) {
			this.unmarshall();
			stream.configImpl().messageRecipient().processMessage(
				stream,
				stream.unmarshall(_payLoad));
		}
		return true;
	}
}