/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;

public final class MGetInternalIDs extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		YapReader bytes = this.getByteLoad();
		long[] ids;
		YapStream stream = getStream();
		synchronized (stream.i_lock) {
			try {
				ids = stream.getYapClass(bytes.readInt()).getIDs(getTransaction());
			} catch (Exception e) {
				ids = new long[0];
			}
		}
		int size = ids.length;
		MsgD message = Msg.ID_LIST.getWriterForLength(getTransaction(), YapConst.ID_LENGTH * (size + 1));
		YapReader writer = message.payLoad();
		writer.writeInt(size);
		for (int i = 0; i < size; i++) {
			writer.writeInt((int) ids[i]);
		}
		serverThread.write(message);
		return true;
	}
}