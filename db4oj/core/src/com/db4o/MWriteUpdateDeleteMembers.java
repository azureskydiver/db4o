/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MWriteUpdateDeleteMembers extends MsgD {
	public MWriteUpdateDeleteMembers() {
		super();
	}

	public MWriteUpdateDeleteMembers(MsgCloneMarker marker) {
		super(marker);
	}

	final boolean processMessageAtServer(YapSocket sock) {
	    YapStream stream = getStream();
		synchronized (stream.i_lock) {
			this.getTransaction().writeUpdateDeleteMembers(
			    readInt(),
				stream.getYapClass(readInt()),
				readInt(),
				readInt()
                );
		}
		return true;
	}
	
	public Object shallowClone() {
		return shallowCloneInternal(new MWriteUpdateDeleteMembers(MsgCloneMarker.INSTANCE));
	}
}