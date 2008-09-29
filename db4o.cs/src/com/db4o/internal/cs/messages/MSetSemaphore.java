/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;

public final class MSetSemaphore extends MsgD implements ServerSideMessage {
	
	public final boolean processAtServer() {
		int timeout = readInt();
		String name = readString();
		LocalObjectContainer stream = (LocalObjectContainer)stream();
		boolean res = stream.setSemaphore(transaction(), name, timeout);
		if(res){
			write(Msg.SUCCESS);
		}else{
			write(Msg.FAILED);
		}
		return true;
	}

}