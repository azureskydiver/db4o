/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;
import com.db4o.internal.query.result.*;


/**
 * @exclude
 */
public abstract class MObjectSet extends MsgD {
	
	protected AbstractQueryResult queryResult(ServerMessageDispatcher serverThread, int queryResultID){
		return stub(serverThread, queryResultID).queryResult();
	}

	protected LazyClientObjectSetStub stub(ServerMessageDispatcher serverThread, int queryResultID) {
		return serverThread.queryResultForID(queryResultID);
	}

}
