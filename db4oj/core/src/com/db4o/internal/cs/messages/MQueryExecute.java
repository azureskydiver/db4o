/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;

public final class MQueryExecute extends MsgQuery implements ServerSideMessage {
	
	private QueryEvaluationMode _evaluationMode;
	
	public boolean processAtServer() {
		try {
			unmarshall(_payLoad._offset);
			synchronized (streamLock()) {
				writeQueryResult(execute(), _evaluationMode);
			}
		} catch (Db4oException e) {
			writeException(e);
		}
		return true;
	}

	private AbstractQueryResult execute() {
		
		synchronized (streamLock()) {
            
            // TODO: The following used to run outside of the
            // synchronisation block for better performance but
            // produced inconsistent results, cause unknown.

            QQuery query = (QQuery) readObjectFromPayLoad();
            query.unmarshall(transaction());
            
            _evaluationMode = query.evaluationMode();
            
			return executeFully(query);
			
		}
	}

	private AbstractQueryResult executeFully(QQuery query) {
		AbstractQueryResult qr = newQueryResult(query.evaluationMode());
		qr.loadFromQuery(query);
		return qr;
	}
	
}