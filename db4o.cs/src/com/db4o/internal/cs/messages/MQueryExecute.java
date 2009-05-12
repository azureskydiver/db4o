/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.cs.objectexchange.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;

public final class MQueryExecute extends MsgQuery implements ServerSideMessage {
	
	public boolean processAtServer() {
		try {
			unmarshall(_payLoad._offset);
			
			stream().withTransaction(transaction(), new Runnable() { public void run() {
				
				final QQuery query = unmarshallQuery();
				writeQueryResult(executeFully(query), query.evaluationMode(), new ObjectExchangeConfiguration(query.prefetchDepth(), query.prefetchCount()));
				
			}});
		} catch (Db4oException e) {
			writeException(e);
		}
		return true;
	}

	private QQuery unmarshallQuery() {
	    // TODO: The following used to run outside of the
        // Synchronization block for better performance but
        // produced inconsistent results, cause unknown.

        QQuery query = (QQuery) readObjectFromPayLoad();
        query.unmarshall(transaction());
	    return query;
    }

	private AbstractQueryResult executeFully(QQuery query) {
		AbstractQueryResult qr = newQueryResult(query.evaluationMode());
		qr.loadFromQuery(query);
		return qr;
	}
	
}