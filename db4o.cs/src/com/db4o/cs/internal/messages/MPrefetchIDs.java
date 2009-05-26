/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.*;

public final class MPrefetchIDs extends MsgD implements MessageWithResponse {

	public final boolean processAtServer() {
		int prefetchIDCount = readInt();
		MsgD reply = Msg.ID_LIST.getWriterForLength(transaction(), Const4.INT_LENGTH
			* prefetchIDCount);

		synchronized (streamLock()) {
			for (int i = 0; i < prefetchIDCount; i++) {
				reply.writeInt(((LocalObjectContainer) stream()).prefetchID());
			}
		}
		write(reply);
		return true;
	}
}