/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;
import com.db4o.inside.query.QueryResult;

public final class MGetAll extends Msg {
	
	public final boolean processMessageAtServer(YapSocket sock) {
		writeQueryResult(getAll(), sock);
		return true;
	}

	private QueryResult getAll() {
		synchronized (streamLock()) {
			try {
				return getStream().getAll(getTransaction());
			} catch (Exception e) {
				if(Debug.atHome){
					e.printStackTrace();
				}
			}
			return getStream().newQueryResult(getTransaction());
		}
	}
}