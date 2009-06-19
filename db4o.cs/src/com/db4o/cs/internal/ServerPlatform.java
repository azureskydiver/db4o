/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */

package com.db4o.cs.internal;

import com.db4o.cs.internal.messages.*;
import com.db4o.internal.events.*;

/**
 * @sharpen.ignore
 */
public class ServerPlatform {

	public static void triggerMessageEvent(Event4Impl e, Message message) {
		if (!e.hasListeners()) {
			return;
		}
    	e.trigger(new MessageEventArgs(message));
    }

	public static void triggerClientConnectionEvent(Event4Impl e, ClientConnection connection) {
		e.trigger(new ClientConnectionEventArgs(connection));
    }

}
