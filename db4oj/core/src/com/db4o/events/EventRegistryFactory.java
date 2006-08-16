/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

import com.db4o.ObjectContainer;
import com.db4o.YapStream;
import com.db4o.events.impl.EventRegistryImpl;
import com.db4o.foundation.ArgumentNullException;
import com.db4o.inside.callbacks.Callbacks;
import com.db4o.inside.callbacks.NullCallbacks;

/**
 * Provides an interface for getting an {@link EventRegistry} from an {@link ObjectContainer}. 
 */
public class EventRegistryFactory {
	
	/**
	 * Returns an {@link EventRegistry} for registering events with the specified container.
	 */
	public static EventRegistry forObjectContainer(ObjectContainer container) {
		if (null == container) {
			throw new ArgumentNullException("container");
		}
		
		YapStream stream = ((YapStream)container);
		Callbacks callbacks = stream.callbacks();
		if (callbacks instanceof EventRegistry) {
			return (EventRegistry)callbacks;
		}		
		if (callbacks instanceof NullCallbacks) {
			EventRegistryImpl impl = new EventRegistryImpl();
			stream.callbacks(impl);
			return impl;
		}
		
		// TODO: create a MulticastingCallbacks and register both
		// the current one and the new one
		throw new IllegalArgumentException("container callbacks already in use");
	}
}
