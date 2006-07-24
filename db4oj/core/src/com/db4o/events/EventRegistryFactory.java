package com.db4o.events;

import com.db4o.ObjectContainer;
import com.db4o.YapStream;
import com.db4o.events.impl.EventRegistryImpl;
import com.db4o.inside.callbacks.Callbacks;
import com.db4o.inside.callbacks.NullCallbacks;

public class EventRegistryFactory {
	
	public static EventRegistry forObjectContainer(ObjectContainer container) {
		if (null == container) {
			throw new IllegalArgumentException("container can't be null");
		}
		
		YapStream stream = ((YapStream)container);
		Callbacks eventRegistry = stream.callbacks();
		if (eventRegistry instanceof EventRegistry) {
			return (EventRegistry)eventRegistry;
		}		
		if (eventRegistry instanceof NullCallbacks) {
			EventRegistryImpl impl = new EventRegistryImpl();
			stream.callbacks(impl);
			return impl;
		}
		
		// TODO: create a MulticastEventRegistry and register both
		// the current one and the new one
		throw new IllegalArgumentException();
	}
}
