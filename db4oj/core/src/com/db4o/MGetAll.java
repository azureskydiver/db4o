/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;
import com.db4o.inside.query.QueryResult;

final class MGetAll extends Msg {
	final boolean processMessageAtServer(YapSocket sock) {
		YapStream stream = getStream();
		this.writeQueryResult(getTransaction(), getAll(stream), sock);
		return true;
	}

	private QueryResult getAll(YapStream stream) {
		QueryResult qr;
		synchronized (stream.i_lock) {
			try {
				qr = stream.getAll(getTransaction());
			} catch (Exception e) {
				qr = new QueryResultImpl(getTransaction());
			}
		}
		return qr;
	}
}