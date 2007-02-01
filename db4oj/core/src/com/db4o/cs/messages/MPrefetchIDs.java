/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.*;

public final class MPrefetchIDs extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		int prefetchIDCount = readInt();
		MsgD reply =
			Msg.ID_LIST.getWriterForLength(
				transaction(),
				YapConst.INT_LENGTH * prefetchIDCount);

		synchronized (streamLock()) {
			for (int i = 0; i < prefetchIDCount; i++) {
				reply.writeInt(((YapFile)stream()).prefetchID());
			}
		}
		serverThread.write(reply);
		return true;
	}
}