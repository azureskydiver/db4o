/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MPrefetchIDs extends Msg {
	final boolean processMessageAtServer(YapSocket sock) {
		YapFile stream = (YapFile) getStream();
		int prefetchIDCount = stream.config().prefetchIDCount();
		MsgD reply =
			Msg.ID_LIST.getWriterForLength(
				getTransaction(),
				YapConst.INT_LENGTH * prefetchIDCount);

		synchronized (stream.i_lock) {
			for (int i = 0; i < prefetchIDCount; i++) {
				reply.writeInt(stream.prefetchID());
			}
		}
		reply.write(stream, sock);
		return true;
	}
}