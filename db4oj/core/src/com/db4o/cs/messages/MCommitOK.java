/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.foundation.network.YapSocket;

public final class MCommitOK extends Msg {
	public final boolean processMessageAtServer(YapSocket in) {
        getTransaction().commit();
        Msg.OK.write(getStream(), in);
        return true;
    }
}