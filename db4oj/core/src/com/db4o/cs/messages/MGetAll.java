/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;
import com.db4o.inside.query.QueryResult;

public final class MGetAll extends Msg {
	public final boolean processMessageAtServer(YapSocket sock) {
		YapStream stream = getStream();
		writeQueryResult(getAll(stream), sock);
		return true;
	}

	private QueryResult getAll(YapStream stream) {
		QueryResult qr;
		synchronized (stream.i_lock) {
			try {
				qr = stream.getAll(getTransaction());
			} catch (Exception e) {
				qr = stream.createQResult(getTransaction());
			}
		}
		return qr;
	}
}