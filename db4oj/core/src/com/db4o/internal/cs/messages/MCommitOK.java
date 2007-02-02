/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;

public final class MCommitOK extends Msg {
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
        transaction().commit();
        serverThread.write(Msg.OK);
        return true;
    }
}