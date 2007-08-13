package com.db4o.internal.cs.messages;

/**
 * @exclude
 */
public class MCloseSocket extends Msg implements ServerSideMessage {

	public boolean processAtServer() {
		synchronized (stream().lock()) {
			if (stream().isClosed()) {
				return true;
			}
			transaction().commit();
			logMsg(35, serverMessageDispatcher().name());
			serverMessageDispatcher().closeConnection();
		}
		return true;
	}
}
