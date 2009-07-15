/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.*;
import com.db4o.internal.*;

public final class MGetClasses extends MsgD implements MessageWithResponse {
	public final Msg replyFromServer() {
	    ObjectContainerBase stream = stream();
		synchronized (streamLock()) {
			try {

				// Since every new Client reads the class
				// collection from the file, we have to 
				// make sure, it has been written.
				stream.classCollection().write(transaction());

			} catch (Exception e) {
				if (Deploy.debug) {
					System.out.println("Msg.GetConfig failed.");
				}
			}
		}
		MsgD message = Msg.GET_CLASSES.getWriterForLength(transaction(), Const4.INT_LENGTH + 1);
		ByteArrayBuffer writer = message.payLoad();
		writer.writeInt(stream.classCollection().getID());
		writer.writeByte(stream.stringIO().encodingByte());
		return message;
	}
}