/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.*;

final class MQueryExecute extends MsgObject {
	boolean processMessageAtServer(YapSocket sock) {
		Transaction trans = getTransaction();
		YapStream stream = getStream();
		QueryResultImpl qr = new QueryResultImpl(trans);
		this.unmarshall();
		QQuery query = (QQuery) stream.unmarshall(payLoad);
		query.unmarshall(getTransaction());
		synchronized (stream.i_lock) {
			try {
				query.executeLocal(qr);
			} catch (Exception e) {
				// 
				qr = new QueryResultImpl(getTransaction());
			}
		}
		writeQueryResult(getTransaction(), qr, sock);
		return true;
	}
}