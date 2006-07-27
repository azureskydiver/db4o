package com.db4o.events.impl;

import com.db4o.events.*;
import com.db4o.inside.callbacks.Callbacks;
import com.db4o.query.Query;

public class EventRegistryImpl implements Callbacks, EventRegistry {
	
	private final Event4Impl _queryStarted = new Event4Impl();
	private final Event4Impl _queryFinished = new Event4Impl();
	
	private final Event4Impl _objectCanNew = new Event4Impl();
	private final Event4Impl _objectCanActivate = new Event4Impl();
	
	private final Event4Impl _objectOnActivate = new Event4Impl();
	private final Event4Impl _objectOnNew = new Event4Impl();

	// Callbacks implementation
	public void onQueryFinished(Query query) {
		_queryFinished.trigger(new QueryEventArgsImpl(query));
	}

	public void onQueryStarted(Query query) {
		_queryStarted.trigger(new QueryEventArgsImpl(query));
	}
	
	public boolean objectCanNew(Object obj) {
		return triggerCancellableEvent(_objectCanNew, obj);
	}

	private boolean triggerCancellableEvent(Event4Impl event, Object obj) {
		CancellableObjectEventArgsImpl args = new CancellableObjectEventArgsImpl(obj);
		event.trigger(args);
		return !args.isCancelled();
	}
	
	public boolean objectCanActivate(Object obj) {
		return triggerCancellableEvent(_objectCanActivate, obj);
	}
	
	public void objectOnActivate(Object obj) {
		_objectOnActivate.trigger(new ObjectEventArgsImpl(obj));
	}
	
	public void objectOnNew(Object obj) {
		_objectOnNew.trigger(new ObjectEventArgsImpl(obj));
	}

	// EventRegistry implementation
	public Event4 queryFinished() {
		return _queryFinished;
	}

	public Event4 queryStarted() {
		return _queryStarted;
	}
	
	public Event4 objectCanNew() {
		return _objectCanNew;
	}
	
	public Event4 objectCanActivate() {
		return _objectCanActivate;
	}

	public Event4 objectOnActivate() {
		return _objectOnActivate;
	}

	public Event4 objectOnNew() {
		return _objectOnNew;
	}
}
