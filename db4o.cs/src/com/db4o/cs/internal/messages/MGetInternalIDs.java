/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.cs.internal.objectexchange.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

public final class MGetInternalIDs extends MsgD implements MessageWithResponse {
	public final boolean processAtServer() {
		
		ByteArrayBuffer bytes = getByteLoad();
        final int classMetadataID = bytes.readInt();
        final int prefetchDepth = bytes.readInt();
        final int prefetchCount = bytes.readInt();
        
		final ByteArrayBuffer payload = marshallIDsFor(classMetadataID,
				prefetchDepth, prefetchCount);
		final MsgD message = Msg.ID_LIST.getWriterForLength(transaction(), payload.length());
		message.payLoad().writeBytes(payload._buffer);
		
		write(message);
		
		return true;
	}

	private ByteArrayBuffer marshallIDsFor(final int classMetadataID,
			final int prefetchDepth, final int prefetchCount) {
		synchronized(streamLock()){
			final long[] ids = idsFor(classMetadataID);
			
			return ObjectExchangeStrategyFactory.forConfig(
					new ObjectExchangeConfiguration(prefetchDepth, prefetchCount)
				).marshall((LocalTransaction)transaction(), IntIterators.forLongs(ids), ids.length);
		}
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