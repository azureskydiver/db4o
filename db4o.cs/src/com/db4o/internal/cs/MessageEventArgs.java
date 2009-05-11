package com.db4o.internal.cs;

import com.db4o.events.*;
import com.db4o.internal.cs.messages.*;

public class MessageEventArgs extends EventArgs {

	private Msg _message;

	public MessageEventArgs(Msg message) {
	    _message = message;
    }

	public Msg message() {
		return _message;
    }

}
