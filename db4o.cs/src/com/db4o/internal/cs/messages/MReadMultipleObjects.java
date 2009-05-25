/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.objectexchange.*;

public class MReadMultipleObjects extends MsgD implements MessageWithResponse {
	
	public final boolean processAtServer() {
		int prefetchDepth = readInt();
		int prefetchCount = readInt();
		IntIterator4 ids = new FixedSizeIntIterator4Base(prefetchCount) {
			@Override
			protected int nextInt() {
				return readInt();
			}
		};
		
		final ObjectExchangeStrategy strategy = ObjectExchangeStrategyFactory.forConfig(new ObjectExchangeConfiguration(prefetchDepth, prefetchCount));
		ByteArrayBuffer buffer = strategy.marshall((LocalTransaction) transaction(), ids, prefetchCount);
		
		MsgD msg = Msg.READ_MULTIPLE_OBJECTS.getWriterForBuffer(transaction(), buffer);
		write(msg);
		return true;
	}
}