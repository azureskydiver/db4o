/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public final class MReadMultipleObjects extends MsgD {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		int size = readInt();
		MsgD[] ret = new MsgD[size];
		int length = (1 + size) * Const4.INT_LENGTH;
		synchronized (streamLock()) {
			for (int i = 0; i < size; i++) {
				int id = this._payLoad.readInt();
				try {
					StatefulBuffer bytes = stream().readWriterByID(transaction(),id);
					if(bytes != null){
						ret[i] = Msg.OBJECT_TO_CLIENT.getWriter(bytes);
						length += ret[i]._payLoad.getLength();
					}
				} catch (Exception e) {
					if(Debug.atHome){
						e.printStackTrace();
					}
				}
			}
		}
		
		MsgD multibytes = Msg.READ_MULTIPLE_OBJECTS.getWriterForLength(transaction(), length);
		multibytes.writeInt(size);
		for (int i = 0; i < size; i++) {
			if(ret[i] == null){
				multibytes.writeInt(0);
			}else{
				multibytes.writeInt(ret[i]._payLoad.getLength());
				multibytes._payLoad.append(ret[i]._payLoad._buffer);
			}
		}
		serverThread.write(multibytes);
		return true;
	}
}