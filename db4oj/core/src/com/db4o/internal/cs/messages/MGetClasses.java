/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public final class MGetClasses extends MsgD {
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
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
		Buffer writer = message.payLoad();
		writer.writeInt(stream.classCollection().getID());
		writer.append(stream.stringIO().encodingByte());
		serverThread.write(message);
		return true;
	}
}