package com.db4o.events.impl;

import com.db4o.events.*;
import com.db4o.inside.callbacks.Callbacks;
import com.db4o.query.Query;

public class EventRegistryImpl implements Callbacks, EventRegistry {
	
	private final Event4Impl _queryStarted = new Event4Impl();
	private final Event4Impl _queryFinished = new Event4Impl();	

	// Callbacks implementation
	public void onQueryFinished(Query query) {
		_queryFinished.trigger(query);
	}

	public void onQueryStarted(Query query) {
		_queryStarted.trigger(query);
	}

	// EventRegistry implementation
	public Event4 queryFinished() {
		return _queryFinished;
	}

	public Event4 queryStarted() {
		return _queryStarted;
	}
}
