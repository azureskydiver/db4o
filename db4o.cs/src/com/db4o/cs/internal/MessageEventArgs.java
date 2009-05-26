package com.db4o.cs.internal;

import com.db4o.cs.internal.messages.*;
import com.db4o.events.*;

public class MessageEventArgs extends EventArgs {

	private Msg _message;

	public MessageEventArgs(Msg message) {
	    _message = message;
    }

	public Msg message() {
		return _message;
    }

}
