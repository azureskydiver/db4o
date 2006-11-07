/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.query.*;

public final class MGetAll extends MsgQuery {
	
	public final boolean processAtServer(YapServerThread serverThread) {
		boolean lazy = readBoolean();
		writeQueryResult(getAll(lazy), serverThread, lazy);
		return true;
	}

	private AbstractQueryResult getAll(boolean lazy) {
		synchronized (streamLock()) {
			try {
				return file().getAll(transaction(), lazy);
			} catch (Exception e) {
				if(Debug.atHome){
					e.printStackTrace();
				}
			}
			return newQueryResult(false);
		}
	}
}