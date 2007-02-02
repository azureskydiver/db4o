/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;

public final class MWriteUpdateDeleteMembers extends MsgD {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		synchronized (streamLock()) {
			transaction().writeUpdateDeleteMembers(
			    readInt(),
				stream().getYapClass(readInt()),
				readInt(),
				readInt()
                );
		}
		return true;
	}
}