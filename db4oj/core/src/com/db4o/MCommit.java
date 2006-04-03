/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MCommit extends Msg {
	public MCommit() {
		super();
	}

	public MCommit(MsgCloneMarker marker) {
		super(marker);
	}

	final boolean processMessageAtServer(YapSocket in) {
		getTransaction().commit();
		return true;
	}
	
	public Object shallowClone() {
		return shallowCloneInternal(new MCommit(MsgCloneMarker.INSTANCE));
	}
}