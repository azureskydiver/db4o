/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MSetSemaphore extends MsgD {
	final boolean processMessageAtServer(YapSocket sock) {
		int timeout = readInt();
		String name = readString();
		YapFile stream = (YapFile)getStream();
		boolean res = stream.setSemaphore(getTransaction(), name, timeout);
		if(res){
			Msg.SUCCESS.write(stream, sock);
		}else{
			Msg.FAILED.write(stream, sock);
		}
		return true;
	}
}