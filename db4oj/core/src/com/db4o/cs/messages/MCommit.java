/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.foundation.network.YapSocket;

final class MCommit extends Msg {
	public final boolean processMessageAtServer(YapSocket in) {
		getTransaction().commit();
		return true;
	}
}