/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;

import com.db4o.internal.*;

public final class MDelete extends MsgD implements ServerSideMessage {
	public final boolean processAtServer() {
		ByteArrayBuffer bytes = this.getByteLoad();
		ObjectContainerBase stream = stream();
		synchronized (streamLock()) {
			Object obj = stream.tryGetByID(transaction(), bytes.readInt());
            boolean userCall = bytes.readInt() == 1;
			if (obj != null) {
				try {
				    stream.delete1(transaction(), obj, userCall);
				} catch (Exception e) {
					if (Debug.atHome) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}
}