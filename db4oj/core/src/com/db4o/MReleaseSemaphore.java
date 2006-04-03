/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.network.YapSocket;

final class MReleaseSemaphore extends MsgD {
	public MReleaseSemaphore() {
		super();
	}

	public MReleaseSemaphore(MsgCloneMarker marker) {
		super(marker);
	}

	final boolean processMessageAtServer(YapSocket sock) {
		String name = readString();
		((YapFile)getStream()).releaseSemaphore(getTransaction(),name);
		return true;
	}
    
    public Object shallowClone() {
    	return super.shallowCloneInternal(new MReleaseSemaphore(MsgCloneMarker.INSTANCE));
    }
}