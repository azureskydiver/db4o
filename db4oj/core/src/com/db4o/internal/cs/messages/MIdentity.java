/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class MIdentity extends Msg implements ServerSideMessage {
	public boolean processAtServer() {
		ObjectContainerBase stream = stream();
		respondInt(stream.getID(transaction(), ((InternalObjectContainer)stream).identity()));
		return true;
	}
}
