/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.query.result.*;

public final class MGetAll extends MsgQuery implements ServerSideMessage {
	
	public final boolean processAtServer() {
		QueryEvaluationMode evaluationMode = QueryEvaluationMode.fromInt(readInt());
		writeQueryResult(getAll(evaluationMode), evaluationMode);
		return true;
	}

	private AbstractQueryResult getAll(QueryEvaluationMode mode) {
		synchronized (streamLock()) {
			try {
				return file().getAll(transaction(), mode);
			} catch (Exception e) {
				if(Debug.atHome){
					e.printStackTrace();
				}
			}
			return newQueryResult(mode);
		}
	}
	
}