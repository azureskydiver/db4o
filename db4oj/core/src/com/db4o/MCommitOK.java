/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MCommitOK extends Msg {
	public MCommitOK() {
		super();
	}

	public MCommitOK(MsgCloneMarker marker) {
		super(marker);
	}

    final boolean processMessageAtServer(YapSocket in) {
        getTransaction().commit();
        Msg.OK.write(getStream(), in);
        return true;
    }

	public Object shallowClone() {
		return shallowCloneInternal(new MCommitOK(MsgCloneMarker.INSTANCE));
	}
}