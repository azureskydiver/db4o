package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public class MReadReaderById extends MsgD implements ServerSideMessage {
	
	public final boolean processAtServer() {
		ByteArrayBuffer bytes = null;
		// readWriterByID may fail in certain cases, for instance if
		// and object was deleted by another client
		synchronized (streamLock()) {
			try {
				bytes = stream().readReaderByID(transaction(), _payLoad.readInt(), _payLoad.readInt()==1);
			} catch (Db4oException e) {
				writeException(e);
				return true;
			} catch (OutOfMemoryError oome){
				writeException(new InternalServerError(oome));
				return true;
			}
		}
		if (bytes == null) {
			bytes = new ByteArrayBuffer(0);
		}
		write(Msg.READ_BYTES.getWriter(transaction(), bytes));
		return true;
	}
}