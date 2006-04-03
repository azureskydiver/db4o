/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MRollback extends Msg {
	public MRollback() {
		super();
	}

	public MRollback(MsgCloneMarker marker) {
		super(marker);
	}

	final boolean processMessageAtServer(YapSocket sock) {
		this.getTransaction().rollback();
		return true;
	}

	public Object shallowClone() {
		return shallowCloneInternal(new MRollback(MsgCloneMarker.INSTANCE));
	}
}