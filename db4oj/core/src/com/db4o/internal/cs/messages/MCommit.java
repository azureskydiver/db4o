/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.cs.*;

final class MCommit extends Msg {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		try{
			transaction().commit();
		}catch(Db4oException db4oException){
			serverThread.write(MCommitResponse.createWithException(transaction(), db4oException));
			return true;
		}
		serverThread.write(MCommitResponse.createWithoutException(transaction()));
		return true;
	}
	
}