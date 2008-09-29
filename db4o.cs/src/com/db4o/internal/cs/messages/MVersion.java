/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class MVersion extends Msg implements ServerSideMessage {

	public boolean processAtServer() {
		long ver = 0;
		ObjectContainerBase stream = stream();
		synchronized (streamLock()) {
			ver = stream.currentVersion();
		}
		write(Msg.ID_LIST.getWriterForLong(transaction(), ver));
		return true;
	}
}
