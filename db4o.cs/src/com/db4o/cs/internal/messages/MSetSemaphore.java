/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.*;

public final class MSetSemaphore extends MsgD implements MessageWithResponse {
	
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