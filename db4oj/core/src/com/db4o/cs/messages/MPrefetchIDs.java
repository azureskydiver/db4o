/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;

public final class MPrefetchIDs extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		YapFile stream = (YapFile) getStream();
		int prefetchIDCount = readInt();
		MsgD reply =
			Msg.ID_LIST.getWriterForLength(
				getTransaction(),
				YapConst.INT_LENGTH * prefetchIDCount);

		synchronized (stream.i_lock) {
			for (int i = 0; i < prefetchIDCount; i++) {
				reply.writeInt(stream.prefetchID());
			}
		}
		serverThread.write(reply);
		return true;
	}
}