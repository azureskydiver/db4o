/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;

public final class MSetSemaphore extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		int timeout = readInt();
		String name = readString();
		YapFile stream = (YapFile)getStream();
		boolean res = stream.setSemaphore(getTransaction(), name, timeout);
		if(res){
			serverThread.write(Msg.SUCCESS);
		}else{
			serverThread.write(Msg.FAILED);
		}
		return true;
	}
}