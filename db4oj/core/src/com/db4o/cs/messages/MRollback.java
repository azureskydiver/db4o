/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.foundation.network.YapSocket;

public final class MRollback extends Msg {
	public final boolean processMessageAtServer(YapSocket sock) {
		this.getTransaction().rollback();
		return true;
	}
}