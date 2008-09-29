/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.query.result.*;


/**
 * @exclude
 */
public class MObjectSetIndexOf extends MObjectSet implements ServerSideMessage {
	
	public boolean processAtServer() {
		AbstractQueryResult queryResult = queryResult(readInt());
		synchronized(streamLock()) {
			int id = queryResult.indexOf(readInt()); 
			write(Msg.OBJECTSET_INDEXOF.getWriterForInt(transaction(), id));
		}
		return true;
	}

}
