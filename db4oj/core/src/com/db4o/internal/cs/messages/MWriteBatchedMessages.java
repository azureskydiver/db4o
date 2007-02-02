/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public class MWriteBatchedMessages extends MsgD {
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		int count = readInt();
		Transaction ta = transaction();
		for (int i = 0; i < count; i++) {
			StatefulBuffer writer = _payLoad.readYapBytes();
			int messageId = writer.readInt();
			Msg message = Msg.getMessage(messageId);
			Msg clonedMessage = message.clone(ta);
			if (clonedMessage instanceof MsgD) {
				MsgD mso = (MsgD) clonedMessage;
				mso.payLoad(writer);
				if (mso.payLoad() != null) {
					mso.payLoad().incrementOffset(Const4.MESSAGE_LENGTH - Const4.INT_LENGTH);
					mso.payLoad().setTransaction(ta);
					mso.processAtServer(serverThread);
				}
			} else { // Msg
				 if(!clonedMessage.processAtServer(serverThread)) {
					 // if the message is not processed in Msg.processAtServer
					 serverThread.processSpecialMsg(clonedMessage);
				 }
			}
		}
		return true;
	}

}
