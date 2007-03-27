/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;

final class MCommit extends Msg implements ServerSideMessage {
	
	public final boolean processAtServer() {
		try{
			transaction().commit();
		}catch(Db4oException db4oException){
			write(MCommitResponse.createWithException(transaction(), db4oException));
			return true;
		}
		write(MCommitResponse.createWithoutException(transaction()));
		return true;
	}
	
}