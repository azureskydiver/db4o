/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MGetAll extends Msg {
	public MGetAll() {
		super();
	}

	public MGetAll(MsgCloneMarker marker) {
		super(marker);
	}

	final boolean processMessageAtServer(YapSocket sock) {
		QueryResultImpl qr;
		YapStream stream = getStream();
		synchronized (stream.i_lock) {
			try {
				qr = (QueryResultImpl)stream.get1(getTransaction(), null)._delegate;
			} catch (Exception e) {
				qr = new QueryResultImpl(getTransaction());
			}
		}
		this.writeQueryResult(getTransaction(), qr, sock);
		return true;
	}

	public Object shallowClone() {
		return shallowCloneInternal(new MGetAll(MsgCloneMarker.INSTANCE));
	}
}