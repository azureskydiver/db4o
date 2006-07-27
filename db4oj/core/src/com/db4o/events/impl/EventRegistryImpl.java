package com.db4o.events.impl;

import com.db4o.events.*;
import com.db4o.inside.callbacks.Callbacks;
import com.db4o.query.Query;

public class EventRegistryImpl implements Callbacks, EventRegistry {
	
	private final Event4Impl _queryStarted = new Event4Impl();
	private final Event4Impl _queryFinished = new Event4Impl();
	
	private final Event4Impl _objectCanNew = new Event4Impl();
	private final Event4Impl _objectCanActivate = new Event4Impl();
	private final Event4Impl _objectCanUpdate = new Event4Impl();
	private final Event4Impl _objectCanDelete = new Event4Impl();
	
	private final Event4Impl _objectOnNew = new Event4Impl();
	private final Event4Impl _objectOnActivate = new Event4Impl();
	private final Event4Impl _objectOnUpdate = new Event4Impl();
	private final Event4Impl _objectOnDelete = new Event4Impl();

	// Callbacks implementation
	public void onQueryFinished(Query query) {
		_queryFinished.trigger(new QueryEventArgsImpl(query));
	}

	public void onQueryStarted(Query query) {
		_queryStarted.trigger(new QueryEventArgsImpl(query));
	}
	
	private boolean triggerCancellableEvent(Event4Impl event, Object obj) {
		CancellableObjectEventArgsImpl args = new CancellableObjectEventArgsImpl(obj);
		event.trigger(args);
		return !args.isCancelled();
	}
	
	private void triggerObjectEvent(Event4Impl event, Object obj) {
		event.trigger(new ObjectEventArgsImpl(obj));
	}
	
	public boolean objectCanNew(Object obj) {
		return triggerCancellableEvent(_objectCanNew, obj);
	}
	
	public boolean objectCanActivate(Object obj) {
		return triggerCancellableEvent(_objectCanActivate, obj);
	}
	
	public boolean objectCanUpdate(Object obj) {
		return triggerCancellableEvent(_objectCanUpdate, obj);
	}
	
	public boolean objectCanDelete(Object obj) {
		return triggerCancellableEvent(_objectCanDelete, obj);
	}
	
	public void objectOnActivate(Object obj) {
		triggerObjectEvent(_objectOnActivate, obj);
	}
	
	public void objectOnNew(Object obj) {
		triggerObjectEvent(_objectOnNew, obj);
	}
	
	public void objectOnUpdate(Object obj) {
		triggerObjectEvent(_objectOnUpdate, obj);
	}
	
	public void objectOnDelete(Object obj) {
		triggerObjectEvent(_objectOnDelete, obj);		
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
	
	public Event4 objectCanUpdate() {
		return _objectCanUpdate;
	}
	
	public Event4 objectCanDelete() {
		return _objectCanDelete;
	}
	
	public Event4 objectOnNew() {
		return _objectOnNew;
	}
	
	public Event4 objectOnActivate() {
		return _objectOnActivate;
	}
	
	public Event4 objectOnUpdate() {
		return _objectOnUpdate;
	}

	public Event4 objectOnDelete() {
		return _objectOnDelete;
	}
}
