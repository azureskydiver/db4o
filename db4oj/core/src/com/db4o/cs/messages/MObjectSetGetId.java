/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;
import com.db4o.inside.query.*;


/**
 * @exclude
 */
public class MObjectSetGetId extends MObjectSet {
	
	public boolean processAtServer(YapServerThread serverThread) {
		AbstractQueryResult queryResult = queryResult(serverThread, readInt());
		int id = queryResult.getId(readInt()); 
		serverThread.write(Msg.OBJECTSET_GET_ID.getWriterForInt(transaction(), id));
		return true;
	}

}
