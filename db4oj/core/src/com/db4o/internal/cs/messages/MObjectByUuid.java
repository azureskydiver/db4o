/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;


/**
 * 
 */
public class MObjectByUuid extends MsgD {
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		long uuid = readLong();
		byte[] signature = readBytes();
		int id = 0;
		Transaction trans = transaction();
		synchronized (streamLock()) {
			try {
				HardObjectReference hardRef = trans.getHardReferenceBySignature(uuid, signature);
			    if(hardRef._reference != null){
			        id = hardRef._reference.getID();
			    }
			} catch (Exception e) {
			    if(Deploy.debug){
			        e.printStackTrace();
			    }
			}
		}
		serverThread.write(Msg.OBJECT_BY_UUID.getWriterForInt(trans, id));
		return true;
	}
}
