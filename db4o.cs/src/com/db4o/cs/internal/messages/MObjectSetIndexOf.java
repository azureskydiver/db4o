/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.query.result.*;


/**
 * @exclude
 */
public class MObjectSetIndexOf extends MObjectSet implements MessageWithResponse {
	
	public boolean processAtServer() {
		AbstractQueryResult queryResult = queryResult(readInt());
		synchronized(streamLock()) {
			int id = queryResult.indexOf(readInt()); 
			write(Msg.OBJECTSET_INDEXOF.getWriterForInt(transaction(), id));
		}
		return true;
	}

}
