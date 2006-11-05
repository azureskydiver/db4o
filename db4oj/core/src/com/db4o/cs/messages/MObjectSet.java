/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;
import com.db4o.inside.query.*;


/**
 * @exclude
 */
public abstract class MObjectSet extends MsgD {
	
	protected AbstractQueryResult queryResult(YapServerThread serverThread, int queryResultID){
		return stub(serverThread, queryResultID).queryResult();
	}

	protected LazyClientObjectSetStub stub(YapServerThread serverThread, int queryResultID) {
		return serverThread.queryResultForID(queryResultID);
	}

}
