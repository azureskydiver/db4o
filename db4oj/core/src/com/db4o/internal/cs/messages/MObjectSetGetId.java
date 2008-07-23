/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.query.result.*;


/**
 * @exclude
 */
public class MObjectSetGetId extends MObjectSet implements ServerSideMessage {
	
	public boolean processAtServer() {
		AbstractQueryResult queryResult = queryResult(readInt());		
		try {
			int id = 0;
			synchronized (streamLock()) {
				id = queryResult.getId(readInt());
			}
			write(Msg.OBJECTSET_GET_ID.getWriterForInt(transaction(), id));
		} catch (IndexOutOfBoundsException e) {
			writeException(e);
		}
		return true;
	}

}
