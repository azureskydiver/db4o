/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;

public final class MCommit extends Msg implements ServerSideMessage {
	
	public final boolean processAtServer() {
		try{
			serverTransaction().commit(serverMessageDispatcher());
			write(Msg.OK);
		}catch(Db4oException e){
			writeException(e);
		}
		return true;
	}
	
}