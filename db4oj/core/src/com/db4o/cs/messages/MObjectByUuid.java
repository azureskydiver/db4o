/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;


/**
 * 
 */
public class MObjectByUuid extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		long uuid = readLong();
		byte[] signature = readBytes();
		int id = 0;
		YapStream stream = getStream();
		Transaction trans = getTransaction();
		synchronized (stream.i_lock) {
			try {
			    Object[] arr = trans.objectAndYapObjectBySignature(uuid, signature);
			    if(arr[1] != null){
			        YapObject yo = (YapObject)arr[1];
			        id = yo.getID();
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
