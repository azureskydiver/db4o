/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class MReadBytes extends MsgD {
	
	final YapWriter getByteLoad() {
		int address = this.payLoad.readInt();
		int length = this.payLoad.getLength() - (YapConst.YAPINT_LENGTH);
		this.payLoad.removeFirstBytes(YapConst.YAPINT_LENGTH);
		this.payLoad.useSlot(address, length);
		return this.payLoad;
	}

	final MsgD getWriter(YapWriter bytes) {
		MsgD message =
			this.getWriterForLength(bytes.getTransaction(), bytes.getLength() + YapConst.YAPINT_LENGTH);
		message.payLoad.writeInt(bytes.getAddress());
		message.payLoad.append(bytes.i_bytes);
		return message;
	}
	
	final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		int address = this.readInt();
		int length = this.readInt();
		synchronized (stream.i_lock) {
			YapWriter bytes =
				new YapWriter(this.getTransaction(), address, length);
			try {
				stream.readBytes(bytes.i_bytes, address, length);
				getWriter(bytes).write(stream, sock);
			} catch (Exception e) {
				// TODO: not nicely handled on the client side yet
				Msg.NULL.write(stream, sock);
			}
		}
		return true;
	}
}