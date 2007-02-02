/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.*;

public final class MReadObject extends MsgD {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		StatefulBuffer bytes = null;

		// readObjectByID may fail in certain cases
		// we should look for the cause at some time in the future

		synchronized (streamLock()) {
			try {
				bytes = stream().readWriterByID(transaction(), _payLoad.readInt());
			} catch (Exception e) {
				if (Deploy.debug) {
					System.out.println("MsD.ReadObject:: readObjectByID failed");
				}
			}
		}
		if (bytes == null) {
			bytes = new StatefulBuffer(transaction(), 0, 0);
		}
		serverThread.write(Msg.OBJECT_TO_CLIENT.getWriter(bytes));
		return true;
	}
}