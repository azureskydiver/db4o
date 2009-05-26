package com.db4o.cs.internal.messages;

import com.db4o.internal.*;

public class MInstanceCount extends MsgD implements MessageWithResponse {

	public boolean processAtServer() {
		MsgD writer = null;
		synchronized(streamLock()) {
			ClassMetadata clazz = file().classMetadataForID(readInt());
			writer = Msg.INSTANCE_COUNT.getWriterForInt(transaction(), clazz.indexEntryCount(transaction()));
		}
		write(writer);
		return true;
	}

}
