/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.objectexchange.*;


/**
 * @exclude
 */
public class MObjectSetFetch extends MObjectSet implements MessageWithResponse {
	
	public boolean processAtServer() {
		int queryResultID = readInt();
		int fetchSize = readInt();
		int fetchDepth = readInt();
		MsgD message = null;
		synchronized(streamLock()) {
			IntIterator4 idIterator = stub(queryResultID).idIterator();
			ByteArrayBuffer payload = ObjectExchangeStrategyFactory.forConfig(new ObjectExchangeConfiguration(fetchDepth, fetchSize)).marshall((LocalTransaction) transaction(), idIterator, fetchSize);
			message = ID_LIST.getWriterForLength(transaction(), payload.length());
			message.writeBytes(payload._buffer);
		}
		write(message);
		return true;
	}

}
