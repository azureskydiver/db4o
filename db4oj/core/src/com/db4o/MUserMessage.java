/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.*;

final class MUserMessage extends MsgObject {
	final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		if (stream.i_config.messageRecipient() != null) {
			this.unmarshall();
			stream.i_config.messageRecipient().processMessage(
				stream,
				stream.unmarshall(payLoad));
		}
		return true;
	}
}