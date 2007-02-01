/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.*;

public final class MDelete extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		Buffer bytes = this.getByteLoad();
		YapStream stream = stream();
		synchronized (streamLock()) {
			Object obj = stream.getByID1(transaction(), bytes.readInt());
            boolean userCall = bytes.readInt() == 1;
			if (obj != null) {
				try {
				    stream.delete1(transaction(), obj, userCall);
				} catch (Exception e) {
					if (Deploy.debug) {
						System.out.println("MsgD.Delete failed.");
					}
				}
			}
		}
		return true;
	}
}