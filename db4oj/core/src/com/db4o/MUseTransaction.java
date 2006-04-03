/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;


final class MUseTransaction extends MsgD {
	// handling in YapServerThread

	public MUseTransaction() {
		super();
	}

	public MUseTransaction(MsgCloneMarker marker) {
		super(marker);
	}

	
	public Object shallowClone() {
		return shallowCloneInternal(new MUseTransaction(MsgCloneMarker.INSTANCE));
	}
}