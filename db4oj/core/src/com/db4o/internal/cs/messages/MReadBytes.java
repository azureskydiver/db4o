/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public final class MReadBytes extends MsgD implements ServerSideMessage {
	
	public final BufferImpl getByteLoad() {
		int address = _payLoad.readInt();
		int length = _payLoad.length() - (Const4.INT_LENGTH);
        Slot slot = new Slot(address, length);
		_payLoad.removeFirstBytes(Const4.INT_LENGTH);
		_payLoad.useSlot(slot);
		return this._payLoad;
	}

	public final MsgD getWriter(StatefulBuffer bytes) {
		MsgD message = getWriterForLength(bytes.getTransaction(), bytes.length() + Const4.INT_LENGTH);
		message._payLoad.writeInt(bytes.getAddress());
		message._payLoad.append(bytes._buffer);
		return message;
	}
	
	public final boolean processAtServer() {
		int address = readInt();
		int length = readInt();
		synchronized (streamLock()) {
			StatefulBuffer bytes =
				new StatefulBuffer(this.transaction(), address, length);
			try {
				stream().readBytes(bytes._buffer, address, length);
				write(getWriter(bytes));
			} catch (Exception e) {
				// TODO: not nicely handled on the client side yet
				write(Msg.NULL);
			}
		}
		return true;
	}
}