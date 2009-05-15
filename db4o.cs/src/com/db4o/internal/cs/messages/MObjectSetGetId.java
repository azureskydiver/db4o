/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.query.result.*;


/**
 * @exclude
 */
public class MObjectSetGetId extends MObjectSet implements MessageWithResponse {
	
	public boolean processAtServer() {
		AbstractQueryResult queryResult = queryResult(readInt());		
		int id = 0;
		synchronized (streamLock()) {
			id = queryResult.getId(readInt());
		}
		write(Msg.OBJECTSET_GET_ID.getWriterForInt(transaction(), id));
		return true;
	}

}
