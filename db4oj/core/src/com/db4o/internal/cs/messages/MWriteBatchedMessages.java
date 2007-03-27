/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;

public class MWriteBatchedMessages extends MsgD implements ServerSideMessage {
	public final boolean processAtServer() {
		int count = readInt();
		Transaction ta = transaction();
		for (int i = 0; i < count; i++) {
			StatefulBuffer writer = _payLoad.readYapBytes();
			int messageId = writer.readInt();
			Msg message = Msg.getMessage(messageId);
			Msg clonedMessage = message.publicClone();
			clonedMessage.setTransaction(ta);
			if (clonedMessage instanceof MsgD) {
				MsgD mso = (MsgD) clonedMessage;
				mso.payLoad(writer);
				if (mso.payLoad() != null) {
					mso.payLoad().incrementOffset(Const4.MESSAGE_LENGTH - Const4.INT_LENGTH);
					mso.payLoad().setTransaction(ta);
					((ServerSideMessage)mso).processAtServer();
				}
			} else {
				((ServerSideMessage)clonedMessage).processAtServer();
			}
		}
		return true;
	}

}
