/* Copyright (C) 2007 Versant Inc. http://www.db4o.com */
package com.db4o.cs.internal.messages;

import com.db4o.cs.internal.*;

public class MCommittedCallBackRegistry extends Msg implements ServerSideMessage {

	public void processAtServer() {
		ServerMessageDispatcher dispatcher = serverMessageDispatcher();
		dispatcher.caresAboutCommitted(true);
	}

}
