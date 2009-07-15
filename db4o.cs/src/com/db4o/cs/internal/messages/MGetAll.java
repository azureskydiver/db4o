/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.internal.objectexchange.*;
import com.db4o.internal.query.result.*;

public final class MGetAll extends MsgQuery implements MessageWithResponse {
	
	public final Msg replyFromServer() {
		QueryEvaluationMode evaluationMode = QueryEvaluationMode.fromInt(readInt());
		int prefetchDepth = readInt();
		int prefetchCount = readInt();
		synchronized(streamLock()) {
			return writeQueryResult(getAll(evaluationMode), evaluationMode, new ObjectExchangeConfiguration(prefetchDepth, prefetchCount));
		}
	}

	private AbstractQueryResult getAll(QueryEvaluationMode mode) {
		try {
			return file().getAll(transaction(), mode);
		} catch (Exception e) {
			if(Debug4.atHome){
				e.printStackTrace();
			}
		}
		return newQueryResult(mode);
	}
	
}