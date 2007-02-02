/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;
import com.db4o.inside.query.*;
import com.db4o.inside.query.result.*;


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
