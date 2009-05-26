/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.cs.internal.*;
import com.db4o.internal.*;

public final class MCommit extends Msg implements MessageWithResponse {
	
	public final boolean processAtServer() {
		CallbackObjectInfoCollections committedInfo = null;
		LocalTransaction serverTransaction = serverTransaction();
		ServerMessageDispatcher dispatcher = serverMessageDispatcher();
		synchronized (streamLock()) {
			serverTransaction.commit(dispatcher);
			committedInfo = dispatcher.committedInfo();
		}
		write(Msg.OK);
		try {
			if (committedInfo != null) {
				addCommittedInfoMsg(committedInfo, serverTransaction);
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		return true;
	}

	private void addCommittedInfoMsg(CallbackObjectInfoCollections committedInfo, LocalTransaction serverTransaction) {
		synchronized (streamLock()) {
			Msg.COMMITTED_INFO.setTransaction(serverTransaction);
			MCommittedInfo message = Msg.COMMITTED_INFO.encode(committedInfo);
			message.setMessageDispatcher(serverMessageDispatcher());
			serverMessageDispatcher().server().addCommittedInfoMsg(message);
		}
	}
	
}