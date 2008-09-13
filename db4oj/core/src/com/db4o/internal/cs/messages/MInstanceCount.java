package com.db4o.internal.cs.messages;

import com.db4o.internal.*;

public class MInstanceCount extends MsgD implements ServerSideMessage {

	public boolean processAtServer() {
		MsgD writer = null;
		synchronized(streamLock()) {
			ClassMetadata clazz = file().classMetadataForId(readInt());
			writer = Msg.INSTANCE_COUNT.getWriterForInt(transaction(), clazz.indexEntryCount(transaction()));
		}
		write(writer);
		return true;
	}

}
