/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MTaIsDeleted extends MsgD {
	public MTaIsDeleted() {
		super();
	}

	public MTaIsDeleted(MsgCloneMarker marker) {
		super(marker);
	}

	final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		synchronized (stream.i_lock) {
			boolean isDeleted = getTransaction().isDeleted(this.readInt());
			int ret = isDeleted ? 1 : 0;
			Msg.TA_IS_DELETED.getWriterForInt(getTransaction(), ret).write(stream, sock);
		}
		return true;
	}
	
	public Object shallowClone() {
		return shallowCloneInternal(new MTaIsDeleted(MsgCloneMarker.INSTANCE));
	}
}