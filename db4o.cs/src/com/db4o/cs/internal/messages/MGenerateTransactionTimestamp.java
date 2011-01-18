package com.db4o.cs.internal.messages;

public class MGenerateTransactionTimestamp extends MsgD implements MessageWithResponse{

	public Msg replyFromServer() {
		long timestamp = transaction().generateTransactionTimestamp();
		return Msg.GENERATE_TRANSACTION_TIMESTAMP.getWriterForLong(transaction(), timestamp);
	}

}
