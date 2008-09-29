/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.internal.cs.messages;

import com.db4o.internal.cs.*;

public class MCommittedCallBackRegistry extends Msg implements ServerSideMessage {

	public boolean processAtServer() {
		ServerMessageDispatcher dispatcher = serverMessageDispatcher();
		dispatcher.caresAboutCommitted(true);
		return true;
	}

}
