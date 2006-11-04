/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;

public final class MReadBytes extends MsgD {
	public final YapReader getByteLoad() {
		int address = this._payLoad.readInt();
		int length = this._payLoad.getLength() - (YapConst.INT_LENGTH);
		this._payLoad.removeFirstBytes(YapConst.INT_LENGTH);
		this._payLoad.useSlot(address, length);
		return this._payLoad;
	}

	public final MsgD getWriter(YapWriter bytes) {
		MsgD message =
			this.getWriterForLength(bytes.getTransaction(), bytes.getLength() + YapConst.INT_LENGTH);
		message._payLoad.writeInt(bytes.getAddress());
		message._payLoad.append(bytes._buffer);
		return message;
	}
	
	public final boolean processAtServer(YapServerThread serverThread) {
	    YapStream stream = getStream();
		int address = this.readInt();
		int length = this.readInt();
		synchronized (stream.i_lock) {
			YapWriter bytes =
				new YapWriter(this.getTransaction(), address, length);
			try {
				stream.readBytes(bytes._buffer, address, length);
				serverThread.write(getWriter(bytes));
			} catch (Exception e) {
				// TODO: not nicely handled on the client side yet
				serverThread.write(Msg.NULL);
			}
		}
		return true;
	}
}