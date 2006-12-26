/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.Transaction;
import com.db4o.YapConst;
import com.db4o.YapWriter;
import com.db4o.cs.YapServerThread;

public class MWriteBatchedObjects extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		int count = readInt();
		Transaction ta = transaction();
		for (int i = 0; i < count; i++) {
			YapWriter writer = _payLoad.readYapBytes();
			int messageId = writer.readInt();
			Msg message = Msg.getMessage(messageId);
			Msg clonedMessage = message.clone(ta);
			if (clonedMessage instanceof MsgObject) {
				MsgObject mso = (MsgObject) clonedMessage;
				mso.payLoad(writer);
				if (mso.payLoad() != null) {
					mso.payLoad().incrementOffset(YapConst.MESSAGE_LENGTH - YapConst.INT_LENGTH);
					mso.payLoad().setTransaction(ta);
					mso.processAtServer(serverThread);
				}
			} // TODO: MsgD handling will be added if there's any in batched messages.
			else {
				 clonedMessage.processAtServer(serverThread);
			}
		}
		return true;
	}

}
