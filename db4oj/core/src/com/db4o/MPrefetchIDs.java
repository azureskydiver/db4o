/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class MPrefetchIDs extends Msg {
	final boolean processMessageAtServer(YapSocket sock) {
		YapFile stream = (YapFile) getStream();
		MsgD reply =
			Msg.ID_LIST.getWriterForLength(
				getTransaction(),
				YapConst.YAPINT_LENGTH * YapConst.PREFETCH_ID_COUNT);

		synchronized (stream.i_lock) {
			for (int i = 0; i < YapConst.PREFETCH_ID_COUNT; i++) {
				reply.writeInt(stream.prefetchID());
			}
		}
		reply.write(stream, sock);
		return true;
	}
}