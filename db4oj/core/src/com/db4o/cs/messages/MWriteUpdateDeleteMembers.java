/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;

public final class MWriteUpdateDeleteMembers extends MsgD {
	public final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		synchronized (stream.i_lock) {
			this.getTransaction().writeUpdateDeleteMembers(
			    readInt(),
				stream.getYapClass(readInt()),
				readInt(),
				readInt()
                );
		}
		return true;
	}
}