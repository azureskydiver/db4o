/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.internal.cs.messages.*;
import com.db4o.internal.events.*;

/**
 * @sharpen.ignore
 */
public class ServerPlatform {

	public static void triggerMessageEvent(Event4Impl e, Msg message) {
		if (!e.hasListeners()) {
			return;
		}
    	e.trigger(new MessageEventArgs(message));
    }

}
