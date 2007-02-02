/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;
import com.db4o.inside.query.*;


/**
 * @exclude
 */
public class MObjectSetIndexOf extends MObjectSet {
	
	public boolean processAtServer(ServerMessageDispatcher serverThread) {
		AbstractQueryResult queryResult = queryResult(serverThread, readInt());
		int id = queryResult.indexOf(readInt()); 
		serverThread.write(Msg.OBJECTSET_INDEXOF.getWriterForInt(transaction(), id));
		return true;
	}

}
