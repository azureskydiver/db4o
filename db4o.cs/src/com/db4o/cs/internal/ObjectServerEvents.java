package com.db4o.cs.internal;

import com.db4o.events.*;

public interface ObjectServerEvents {

	/**
	 * @sharpen.event ClientConnectionEventArgs
	 */
	public Event4<ClientConnectionEventArgs> clientConnected();
	
}
