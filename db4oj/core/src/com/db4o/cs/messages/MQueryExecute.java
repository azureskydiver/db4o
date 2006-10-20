/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;
import com.db4o.inside.query.*;

public final class MQueryExecute extends MsgObject {
	
	public boolean processMessageAtServer(YapSocket sock) {
		unmarshall();
        writeQueryResult(execute(), sock);
		return true;
	}

	private QueryResult execute() {
		
		synchronized (streamLock()) {
            
            // TODO: The following used to run outside of the
            // synchronisation block for better performance but
            // produced inconsistent results, cause unknown.
			
			Transaction trans = getTransaction();
			YapStream stream = getStream();

            QQuery query = (QQuery) stream.unmarshall(_payLoad);
            query.unmarshall(trans);
            
			return executeFully(trans, stream, query);
			
		}
	}

	private QueryResult executeFully(Transaction trans, YapStream stream, QQuery query) {
		try {
			QueryResultImpl qr = stream.createQResult(trans);
			query.executeLocal(qr);
			return qr;
		} catch (Exception e) {
			return stream.createQResult(trans); 
		}
	}
	
}