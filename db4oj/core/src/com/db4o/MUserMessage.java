/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MUserMessage extends MsgObject {
	public MUserMessage() {
		super();
	}

	public MUserMessage(MsgCloneMarker marker) {
		super(marker);
	}

	final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		if (stream.i_config.messageRecipient() != null) {
			this.unmarshall();
			stream.i_config.messageRecipient().processMessage(
				stream,
				stream.unmarshall(_payLoad));
		}
		return true;
	}
	
	public Object shallowClone() {
		return shallowCloneInternal(new MUserMessage(MsgCloneMarker.INSTANCE));
	}
}