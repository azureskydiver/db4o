/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;

public final class MDelete extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		YapReader bytes = this.getByteLoad();
		YapStream stream = getStream();
		synchronized (stream.i_lock) {
			Object obj = stream.getByID1(getTransaction(), bytes.readInt());
            boolean userCall = bytes.readInt() == 1;
			if (obj != null) {
				try {
				    stream.delete1(getTransaction(), obj, userCall);
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