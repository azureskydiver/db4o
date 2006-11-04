/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;

public final class MWriteUpdateDeleteMembers extends MsgD {
	
	public final boolean processAtServer(YapServerThread serverThread) {
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