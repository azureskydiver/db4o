/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;


public final class MWriteUpdateDeleteMembers extends MsgD implements ServerSideMessage {
	
	public final boolean processAtServer() {
		synchronized (streamLock()) {
			transaction().writeUpdateDeleteMembers(readInt(),
				stream().classMetadataForId(readInt()), readInt(), readInt());
		}
		return true;
	}
}