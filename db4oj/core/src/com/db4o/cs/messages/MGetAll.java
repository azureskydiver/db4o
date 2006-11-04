/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.query.*;

public final class MGetAll extends MsgQuery {
	
	public final boolean processAtServer(YapServerThread serverThread) {
		writeQueryResult(getAll(), serverThread);
		return true;
	}

	private AbstractQueryResult getAll() {
		synchronized (streamLock()) {
			try {
				return stream().getAll(transaction());
			} catch (Exception e) {
				if(Debug.atHome){
					e.printStackTrace();
				}
			}
			return newQueryResult();
		}
	}
}