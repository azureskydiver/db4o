/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class MUserMessage extends MsgObject {
	final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		if (stream.i_config.i_messageRecipient != null) {
			this.unmarshall();
			stream.i_config.i_messageRecipient.processMessage(
				stream,
				stream.unmarshall(payLoad));
		}
		return true;
	}
}