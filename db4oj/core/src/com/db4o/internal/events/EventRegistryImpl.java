/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.events;

import com.db4o.events.*;
import com.db4o.ext.ObjectInfoCollection;
import com.db4o.internal.callbacks.Callbacks;
import com.db4o.query.Query;

/**
 * @exclude
 */
public class EventRegistryImpl  implements Callbacks, EventRegistry {
	
	protected final Event4Impl _queryStarted = new Event4Impl();
	protected final Event4Impl _queryFinished = new Event4Impl();
	protected final Event4Impl _creating = new Event4Impl();
	protected final Event4Impl _activating = new Event4Impl();
	protected final Event4Impl _updating = new Event4Impl();
	protected final Event4Impl _deleting = new Event4Impl();
	protected final Event4Impl _deactivating = new Event4Impl();
	protected final Event4Impl _created = new Event4Impl();
	protected final Event4Impl _activated = new Event4Impl();
	protected final Event4Impl _updated = new Event4Impl();
	protected final Event4Impl _deleted = new Event4Impl();
	protected final Event4Impl _deactivated = new Event4Impl();
	protected final Event4Impl _committing = new Event4Impl();

	// Callbacks implementation
	public void onQueryFinished(Query query) {
		EventPlatform.triggerQueryEvent(_queryFinished, query);
	}

	public void onQueryStarted(Query query) {
		EventPlatform.triggerQueryEvent(_queryStarted, query);
	}
	
	public boolean objectCanNew(Object obj) {
		return EventPlatform.triggerCancellableObjectEventArgs(_creating, obj);
	}
	
	public boolean objectCanActivate(Object obj) {
		return EventPlatform.triggerCancellableObjectEventArgs(_activating, obj);
	}
	
	public boolean objectCanUpdate(Object obj) {
		return EventPlatform.triggerCancellableObjectEventArgs(_updating, obj);
	}
	
	public boolean objectCanDelete(Object obj) {
		return EventPlatform.triggerCancellableObjectEventArgs(_deleting, obj);
	}
	
	public boolean objectCanDeactivate(Object obj) {
		return EventPlatform.triggerCancellableObjectEventArgs(_deactivating, obj);
	}
	
	public void objectOnActivate(Object obj) {
		EventPlatform.triggerObjectEvent(_activated, obj);
	}
	
	public void objectOnNew(Object obj) {
		EventPlatform.triggerObjectEvent(_created, obj);
	}
	
	public void objectOnUpdate(Object obj) {
		EventPlatform.triggerObjectEvent(_updated, obj);
	}
	
	public void objectOnDelete(Object obj) {
		EventPlatform.triggerObjectEvent(_deleted, obj);		
	}	

	public void objectOnDeactivate(Object obj) {
		EventPlatform.triggerObjectEvent(_deactivated, obj);
	}
	
	public void commitOnStarted(ObjectInfoCollection added, ObjectInfoCollection deleted, ObjectInfoCollection updated) {
		EventPlatform.triggerCommitEvent(_committing, added, deleted, updated);
	}

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
	
	public Event4 committing() {
		return _committing;
	}

	public boolean caresAboutCommit() {
		return EventPlatform.hasListeners(_committing);
	}
}
