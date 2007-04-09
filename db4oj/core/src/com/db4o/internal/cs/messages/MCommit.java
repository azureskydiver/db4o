/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public final class MCommit extends Msg implements ServerSideMessage {
	
	public final boolean processAtServer() {
		synchronized (streamLock()) {
			try {
				CallbackObjectInfoCollections committedInfo = null;
				LocalTransaction serverTransaction = serverTransaction();
				ServerMessageDispatcher dispatcher = serverMessageDispatcher();
				serverTransaction.commit(dispatcher);
				write(Msg.OK);
				committedInfo = dispatcher.committedInfo();
				if (committedInfo != null) {
					addCommittedInfoMsg(committedInfo);
				}
			} catch (Db4oException e) {
				writeException(e);
			}
		}
		return true;
	}

	private void addCommittedInfoMsg(CallbackObjectInfoCollections committedInfo) {
		Msg.COMMITTED_INFO.setTransaction(serverTransaction());
		MCommittedInfo message = Msg.COMMITTED_INFO.encode(committedInfo);
		serverMessageDispatcher().server().addCommittedInfoMsg(message);
	}
	
}