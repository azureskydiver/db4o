/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.objectexchange.*;

public final class MGetInternalIDs extends MsgD implements MessageWithResponse {
	public final boolean processAtServer() {
		
		ByteArrayBuffer bytes = getByteLoad();
        final int classMetadataID = bytes.readInt();
        final int prefetchDepth = bytes.readInt();
        final int prefetchCount = bytes.readInt();
        
		final long[] ids = idsFor(classMetadataID);
		
		final ByteArrayBuffer payload = ObjectExchangeStrategyFactory.forConfig(
				new ObjectExchangeConfiguration(prefetchDepth, prefetchCount)
			).marshall((LocalTransaction)transaction(), IntIterators.forLongs(ids), ids.length);
		final MsgD message = Msg.ID_LIST.getWriterForLength(transaction(), payload.length());
		message.payLoad().writeBytes(payload._buffer);
		
		write(message);
		
		return true;
	}

	private long[] idsFor(final int classMetadataID) {
	    synchronized (streamLock()) {
			try {
				return stream().classMetadataForID(classMetadataID).getIDs(transaction());
			} catch (Exception e) {
			}
		}
		return new long[0];
    }
}