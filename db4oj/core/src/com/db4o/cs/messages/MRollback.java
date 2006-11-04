/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.cs.*;

public final class MRollback extends Msg {
	public final boolean processAtServer(YapServerThread serverThread) {
		this.getTransaction().rollback();
		return true;
	}
}