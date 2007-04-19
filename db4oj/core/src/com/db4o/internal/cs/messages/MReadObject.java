/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.internal.*;

public final class MReadObject extends MsgD implements ServerSideMessage {
	
	public final boolean processAtServer() {
		StatefulBuffer bytes = null;

		// readObjectByID may fail in certain cases, for instance if
		// and object was deleted by another client

		synchronized (streamLock()) {
			try {
				bytes = stream().readWriterByID(transaction(), _payLoad.readInt());
			} catch (Exception e) {
				if (Debug.atHome) {
					System.out.println("MsD.ReadObject:: readObjectByID failed");
				}
			}
		}
		if (bytes == null) {
			bytes = new StatefulBuffer(transaction(), 0, 0);
		}
		write(Msg.OBJECT_TO_CLIENT.getWriter(bytes));
		return true;
	}
}