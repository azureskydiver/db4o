/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.cs.internal.*;
import com.db4o.internal.*;

public final class MCommit extends Msg implements MessageWithResponse {

	private CallbackObjectInfoCollections committedInfo = null;

	public final Msg replyFromServer() {
		ServerMessageDispatcher dispatcher = serverMessageDispatcher();
		synchronized (containerLock()) {
			serverTransaction().commit(dispatcher);
			committedInfo = dispatcher.committedInfo();
		}
		return Msg.OK;
	}

	@Override
	public void postProcessAtServer() {
		try {
			if (committedInfo != null) {
				addCommittedInfoMsg(committedInfo, serverTransaction());
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	private void addCommittedInfoMsg(CallbackObjectInfoCollections committedInfo, LocalTransaction serverTransaction) {
		synchronized (containerLock()) {
			Msg.COMMITTED_INFO.setTransaction(serverTransaction);
			MCommittedInfo message = Msg.COMMITTED_INFO.encode(committedInfo);
			message.setMessageDispatcher(serverMessageDispatcher());
			serverMessageDispatcher().server().addCommittedInfoMsg(message);
		}
	}
	
}