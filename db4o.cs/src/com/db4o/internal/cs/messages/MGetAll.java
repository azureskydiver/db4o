/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.query.result.*;

public final class MGetAll extends MsgQuery implements ServerSideMessage {
	
	public final boolean processAtServer() {
		QueryEvaluationMode evaluationMode = QueryEvaluationMode.fromInt(readInt());
		synchronized(streamLock()) {
			writeQueryResult(getAll(evaluationMode), evaluationMode);
		}
		return true;
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