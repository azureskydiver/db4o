/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;

public final class MReadBytes extends MsgD {
	
	public final YapReader getByteLoad() {
		int address = _payLoad.readInt();
		int length = _payLoad.getLength() - (YapConst.INT_LENGTH);
		_payLoad.removeFirstBytes(YapConst.INT_LENGTH);
		_payLoad.useSlot(address, length);
		return this._payLoad;
	}

	public final MsgD getWriter(YapWriter bytes) {
		MsgD message = getWriterForLength(bytes.getTransaction(), bytes.getLength() + YapConst.INT_LENGTH);
		message._payLoad.writeInt(bytes.getAddress());
		message._payLoad.append(bytes._buffer);
		return message;
	}
	
	public final boolean processAtServer(YapServerThread serverThread) {
		int address = readInt();
		int length = readInt();
		synchronized (streamLock()) {
			YapWriter bytes =
				new YapWriter(this.transaction(), address, length);
			try {
				stream().readBytes(bytes._buffer, address, length);
				serverThread.write(getWriter(bytes));
			} catch (Exception e) {
				// TODO: not nicely handled on the client side yet
				serverThread.write(Msg.NULL);
			}
		}
		return true;
	}
}