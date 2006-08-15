/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MReadMultipleObjects extends MsgD {
	final boolean processMessageAtServer(YapSocket sock) {

		int size = readInt();
		MsgD[] ret = new MsgD[size];
		int length = (1 + size) * YapConst.INT_LENGTH;
		YapStream stream = getStream();

		YapWriter bytes = null;		
		synchronized (stream.i_lock) {
			for (int i = 0; i < size; i++) {
				int id = this._payLoad.readInt();
				try {
					bytes =
						stream.readWriterByID(
							getTransaction(),
							id);
				} catch (Exception e) {
					bytes = null;
				}
				if(bytes != null){
					ret[i] = Msg.OBJECT_TO_CLIENT.getWriter(bytes);
					length += ret[i]._payLoad.getLength();
				}
			}
		}
		
		MsgD multibytes = Msg.READ_MULTIPLE_OBJECTS.getWriterForLength(getTransaction(), length);
		multibytes.writeInt(size);
		for (int i = 0; i < size; i++) {
			if(ret[i] == null){
				multibytes.writeInt(0);
			}else{
				multibytes.writeInt(ret[i]._payLoad.getLength());
				multibytes._payLoad.append(ret[i]._payLoad._buffer);
			}
		}
		multibytes.write(stream, sock);
		return true;
	}
}