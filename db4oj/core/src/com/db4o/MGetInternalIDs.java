/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class MGetInternalIDs extends MsgD {
	final boolean processMessageAtServer(YapSocket sock) {
		YapWriter bytes = this.getByteLoad();
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
		MsgD message = Msg.ID_LIST.getWriterForLength(getTransaction(), YapConst.YAPID_LENGTH * (size + 1));
		YapWriter writer = message.getPayLoad();
		writer.writeInt(size);
		for (int i = 0; i < size; i++) {
			writer.writeInt((int) ids[i]);
		}
		message.write(stream, sock);
		return true;
	}
}