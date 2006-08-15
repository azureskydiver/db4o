/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MGetClasses extends MsgD {
	final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		synchronized (stream.i_lock) {
			try {

				// Since every new Client reads the class
				// collection from the file, we have to 
				// make sure, it has been written.
				stream.i_classCollection.write(getTransaction());

			} catch (Exception e) {
				if (Deploy.debug) {
					System.out.println("Msg.GetConfig failed.");
				}
			}
		}
		MsgD message = Msg.GET_CLASSES.getWriterForLength(getTransaction(), YapConst.INT_LENGTH + 1);
		YapWriter writer = message.getPayLoad();
		writer.writeInt(stream.i_classCollection.getID());
		writer.append(stream.stringIO().encodingByte());
		message.write(stream, sock);
		return true;
	}
}