/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MUserMessage extends MsgObject {
	final boolean processMessageAtServer(YapSocket sock) {
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