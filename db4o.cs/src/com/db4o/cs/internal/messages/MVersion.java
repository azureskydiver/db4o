/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class MVersion extends Msg implements MessageWithResponse {

	public Msg replyFromServer() {
		long ver = 0;
		ObjectContainerBase stream = stream();
		synchronized (streamLock()) {
			ver = stream.currentVersion();
		}
		return Msg.ID_LIST.getWriterForLong(transaction(), ver);
	}
}
