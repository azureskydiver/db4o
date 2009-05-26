package com.db4o.cs.internal.messages;

import com.db4o.internal.*;

public class MReadReaderById extends MsgD implements MessageWithResponse {
	
	public final boolean processAtServer() {
		ByteArrayBuffer bytes = null;
		// readWriterByID may fail in certain cases, for instance if
		// and object was deleted by another client
		synchronized (streamLock()) {
			bytes = stream().readReaderByID(transaction(), _payLoad.readInt(), _payLoad.readInt()==1);
		}
		if (bytes == null) {
			bytes = new ByteArrayBuffer(0);
		}
		write(Msg.READ_BYTES.getWriter(transaction(), bytes));
		return true;
	}
}