/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.events;

import com.db4o.events.*;
import com.db4o.internal.*;
import com.db4o.internal.callbacks.Callbacks;
import com.db4o.query.Query;

/**
 * @exclude
 */
public class EventRegistryImpl  implements Callbacks, EventRegistry {
	
	private final ObjectContainerBase _container;
	
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
	protected final Event4Impl _committed = new CommittedEvent();
	protected final Event4Impl _instantiated = new Event4Impl();
	protected final Event4Impl _classRegistered = new Event4Impl();	
	
	/**
	 * @sharpen.ignore
	 */
	private class CommittedEvent extends Event4Impl {
		protected void onListenerAdded() {
			onCommittedListener();
		}
	};

	public EventRegistryImpl(ObjectContainerBase container) {
		_container = container;
	}

	// Callbacks implementation
	public void queryOnFinished(Query query) {
		EventPlatform.triggerQueryEvent(_queryFinished, query);
	}

	public void queryOnStarted(Query query) {
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

	public void classOnRegistered(ClassMetadata clazz) {
		EventPlatform.triggerClassEvent(_classRegistered, clazz);		
	}	

	public void objectOnDeactivate(Object obj) {
		EventPlatform.triggerObjectEvent(_deactivated, obj);
	}
	
	public void objectOnInstantiate(Object obj) {
		EventPlatform.triggerObjectEvent(_instantiated, obj);
	}
	
	public void commitOnStarted(Object transaction, CallbackObjectInfoCollections objectInfoCollections) {
		EventPlatform.triggerCommitEvent(_committing, transaction, objectInfoCollections);
	}
	
	public void commitOnCompleted(Object transaction, CallbackObjectInfoCollections objectInfoCollections) {
		EventPlatform.triggerCommitEvent(_committed, transaction, objectInfoCollections);
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
	
	/**
	 * @sharpen.event.onAdd onCommittedListener
	 */
	public Event4 committed() {
		return _committed;
	}

	public Event4 classRegistered() {
		return _classRegistered;
	}

	public Event4 instantiated() {
		return _instantiated;
	}
	
	private void onCommittedListener() {
		// TODO: notify the server that we are interested in 
		// committed callbacks
	}

	public boolean caresAboutCommitting() {
		return EventPlatform.hasListeners(_committing);
	}

	public boolean caresAboutCommitted() {
		return EventPlatform.hasListeners(_committed);
	}	
}
