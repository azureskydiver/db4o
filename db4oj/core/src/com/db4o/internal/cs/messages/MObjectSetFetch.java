/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;


/**
 * @exclude
 */
public class MObjectSetFetch extends MObjectSet implements ServerSideMessage {
	
	public boolean processAtServer() {
		int queryResultID = readInt();
		int fetchSize = readInt();
		IntIterator4 idIterator = stub(queryResultID).idIterator();
		MsgD message = ID_LIST.getWriterForLength(transaction(), bufferLength(fetchSize));
		StatefulBuffer writer = message.payLoad();
    	writer.writeIDs(idIterator, fetchSize);
		write(message);
		return true;
	}

	private int bufferLength(int fetchSize) {
		return Const4.INT_LENGTH * (fetchSize + 1);
	}

}
