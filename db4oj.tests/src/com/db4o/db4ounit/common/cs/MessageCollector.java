/**
 * 
 */
package com.db4o.db4ounit.common.cs;

import java.util.*;

import com.db4o.cs.internal.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.events.*;

class MessageCollector  {
	
	public static List<Msg> forServerDispatcher(ServerMessageDispatcher dispatcher) {
		final ArrayList<Msg> _messages = new ArrayList<Msg>();
		dispatcher.messageReceived().addListener(new EventListener4<MessageEventArgs>() {		
			public void onEvent(Event4 e, MessageEventArgs args) {
				_messages.add(args.message());
			}
		});
		return _messages;
	}
}