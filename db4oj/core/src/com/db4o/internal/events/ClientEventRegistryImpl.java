/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.events;

import com.db4o.events.Event4;
import com.db4o.internal.InternalObjectContainer;

/**
 * @sharpen.partial
 */
public class ClientEventRegistryImpl extends EventRegistryImpl {

	public ClientEventRegistryImpl(InternalObjectContainer container) {
		super(container);
	}

	/**
	 * @sharpen.ignore
	 */
	public Event4 deleted() {
		throw new IllegalArgumentException("delete() events are raised only at server side.");
	}
	
	/**
	 * @sharpen.ignore
	 */
	public Event4 deleting() {
		throw new IllegalArgumentException("deleting() events are raised only at server side.");
	}
}
