/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class MIdentity extends Msg implements MessageWithResponse {
	public Msg replyFromServer() {
		ObjectContainerBase stream = stream();
		return respondInt(stream.getID(transaction(), ((InternalObjectContainer)stream).identity()));
	}
}
