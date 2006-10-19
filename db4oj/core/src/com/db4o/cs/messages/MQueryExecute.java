/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;

public final class MQueryExecute extends MsgObject {
	public boolean processMessageAtServer(YapSocket sock) {
		Transaction trans = getTransaction();
		YapStream stream = getStream();
		QueryResultImpl qr = new QueryResultImpl(trans);
		this.unmarshall();

		synchronized (stream.i_lock) {
            
            // TODO: The following used to run outside of the
            // synchronisation block for better performance but
            // produced inconsistent results, cause unknown.

            QQuery query = (QQuery) stream.unmarshall(_payLoad);
            
            query.unmarshall(getTransaction());
			try {
				query.executeLocal(qr);
			} catch (Exception e) {
				qr = new QueryResultImpl(getTransaction());
			}
		}
        writeQueryResult(getTransaction(), qr, sock);
		return true;
	}
}