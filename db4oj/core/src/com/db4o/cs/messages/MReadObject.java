/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.foundation.network.YapSocket;

public final class MReadObject extends MsgD {
	public final boolean processMessageAtServer(YapSocket sock) {
		YapWriter bytes = null;

		// readObjectByID may fail in certain cases
		// we should look for the cause at some time in the future
		
		YapStream stream = getStream();

		synchronized (stream.i_lock) {
			try {
				bytes = stream.readWriterByID(this.getTransaction(), this._payLoad.readInt());
			} catch (Exception e) {
				if (Deploy.debug) {
					System.out.println("MsD.ReadObject:: readObjectByID failed");
				}
			}
		}
		if (bytes == null) {
			bytes = new YapWriter(this.getTransaction(), 0, 0);
		}
		Msg.OBJECT_TO_CLIENT.getWriter(bytes).write(stream, sock);
		return true;
	}
}