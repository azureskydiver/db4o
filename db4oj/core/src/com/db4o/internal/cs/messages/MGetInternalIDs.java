/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public final class MGetInternalIDs extends MsgD {
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		Buffer bytes = this.getByteLoad();
		long[] ids;
		synchronized (streamLock()) {
			try {
				ids = stream().classMetadataForId(bytes.readInt()).getIDs(transaction());
			} catch (Exception e) {
				ids = new long[0];
			}
		}
		int size = ids.length;
		MsgD message = Msg.ID_LIST.getWriterForLength(transaction(), Const4.ID_LENGTH * (size + 1));
		Buffer writer = message.payLoad();
		writer.writeInt(size);
		for (int i = 0; i < size; i++) {
			writer.writeInt((int) ids[i]);
		}
		serverThread.write(message);
		return true;
	}
}