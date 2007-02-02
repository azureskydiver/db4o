/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;


/**
 * @exclude
 */
public class MObjectSetFetch extends MObjectSet {
	
	public boolean processAtServer(ServerMessageDispatcher serverThread) {
		int queryResultID = readInt();
		int fetchSize = readInt();
		IntIterator4 idIterator = stub(serverThread, queryResultID).idIterator();
		MsgD message = ID_LIST.getWriterForLength(transaction(), bufferLength(fetchSize));
		StatefulBuffer writer = message.payLoad();
    	writer.writeIDs(idIterator, fetchSize);
		serverThread.write(message);
		return true;
	}

	private int bufferLength(int fetchSize) {
		return Const4.INT_LENGTH * (fetchSize + 1);
	}

}
