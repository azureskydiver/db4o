/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.query.result.*;


public class MObjectSetSize extends MObjectSet implements ServerSideMessage {
	
	public boolean processAtServer() {
		AbstractQueryResult queryResult = queryResult(readInt());
		write(Msg.OBJECTSET_SIZE.getWriterForInt(transaction(), queryResult.size()));
		return true;
	}
	
}
