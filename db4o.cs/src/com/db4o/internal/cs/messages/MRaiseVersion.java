/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class MRaiseVersion extends MsgD implements ServerSideMessage {

	public boolean processAtServer() {
		long minimumVersion = readLong();
		ObjectContainerBase stream = stream();
		synchronized (stream) {
			stream.raiseVersion(minimumVersion);
		}
		return true;
	}
}
