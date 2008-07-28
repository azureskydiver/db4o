/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.query.result.*;


public class MObjectSetSize extends MObjectSet implements ServerSideMessage {
	
	public boolean processAtServer() {
		MsgD writer = null;
		synchronized(streamLock()) {
			AbstractQueryResult queryResult = queryResult(readInt());
			writer = Msg.OBJECTSET_SIZE.getWriterForInt(transaction(), queryResult.size());
		}
		write(writer);
		return true;
	}
	
}
