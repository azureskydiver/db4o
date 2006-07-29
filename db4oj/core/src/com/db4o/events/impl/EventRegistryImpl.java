/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events.impl;

import com.db4o.events.*;
import com.db4o.inside.callbacks.Callbacks;
import com.db4o.query.Query;

/**
 * @exclude
 */
public class EventRegistryImpl implements Callbacks, EventRegistry {
	
	private final Event4Impl _queryStarted = new Event4Impl();
	private final Event4Impl _queryFinished = new Event4Impl();
	
	private final Event4Impl _creating = new Event4Impl();
	private final Event4Impl _activating = new Event4Impl();
	private final Event4Impl _updating = new Event4Impl();
	private final Event4Impl _deleting = new Event4Impl();
	private final Event4Impl _deactivating = new Event4Impl();
	
	private final Event4Impl _created = new Event4Impl();
	private final Event4Impl _activated = new Event4Impl();
	private final Event4Impl _updated = new Event4Impl();
	private final Event4Impl _deleted = new Event4Impl();
	private final Event4Impl _deactivated = new Event4Impl();

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
		return triggerCancellableEvent(_creating, obj);
	}
	
	public boolean objectCanActivate(Object obj) {
		return triggerCancellableEvent(_activating, obj);
	}
	
	public boolean objectCanUpdate(Object obj) {
		return triggerCancellableEvent(_updating, obj);
	}
	
	public boolean objectCanDelete(Object obj) {
		return triggerCancellableEvent(_deleting, obj);
	}
	
	public boolean objectCanDeactivate(Object obj) {
		return triggerCancellableEvent(_deactivating, obj);
	}
	
	public void objectOnActivate(Object obj) {
		triggerObjectEvent(_activated, obj);
	}
	
	public void objectOnNew(Object obj) {
		triggerObjectEvent(_created, obj);
	}
	
	public void objectOnUpdate(Object obj) {
		triggerObjectEvent(_updated, obj);
	}
	
	public void objectOnDelete(Object obj) {
		triggerObjectEvent(_deleted, obj);		
	}	

	public void objectOnDeactivate(Object obj) {
		triggerObjectEvent(_deactivated, obj);
	}

	// EventRegistry implementation
	public Event4 queryFinished() {
		return _queryFinished;
	}

	public Event4 queryStarted() {
		return _queryStarted;
	}
	
	public Event4 creating() {
		return _creating;
	}
	
	public Event4 activating() {
		return _activating;
	}
	
	public Event4 updating() {
		return _updating;
	}
	
	public Event4 deleting() {
		return _deleting;
	}
	
	public Event4 deactivating() {
		return _deactivating;
	}
	
	public Event4 created() {
		return _created;
	}
	
	public Event4 activated() {
		return _activated;
	}
	
	public Event4 updated() {
		return _updated;
	}

	public Event4 deleted() {
		return _deleted;
	}

	public Event4 deactivated() {
		return _deactivated;
	}
}
