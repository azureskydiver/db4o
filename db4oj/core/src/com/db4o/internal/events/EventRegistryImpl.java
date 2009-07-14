/* Copyright (C) 2006   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.events;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.callbacks.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public class EventRegistryImpl  implements Callbacks, EventRegistry {
	
	private final InternalObjectContainer _container;
	
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
	protected final Event4Impl _closing = new Event4Impl();
	
	/**
	 * @sharpen.ignore
	 */
	protected class CommittedEvent extends Event4Impl {
		protected void onListenerAdded() {
			onCommittedListener();
		}
	}

	public EventRegistryImpl(InternalObjectContainer container) {
		_container = container;
	}

	// Callbacks implementation
	public void queryOnFinished(Transaction transaction, Query query) {
		EventPlatform.triggerQueryEvent(transaction, _queryFinished, query);
	}

	public void queryOnStarted(Transaction transaction, Query query) {
		EventPlatform.triggerQueryEvent(transaction, _queryStarted, query);
	}
	
	public boolean objectCanNew(Transaction transaction, Object obj) {
		return EventPlatform.triggerCancellableObjectEventArgs(transaction, _creating, null, obj);
	}
	
	public boolean objectCanActivate(Transaction transaction, Object obj) {
		return EventPlatform.triggerCancellableObjectEventArgs(transaction, _activating, null, obj);
	}
	
	public boolean objectCanUpdate(Transaction transaction, ObjectInfo objectInfo) {
		return EventPlatform.triggerCancellableObjectEventArgs(transaction, _updating, objectInfo, objectInfo.getObject());
	}
	
	public boolean objectCanDelete(Transaction transaction, ObjectInfo objectInfo) {
		return EventPlatform.triggerCancellableObjectEventArgs(transaction, _deleting, objectInfo, objectInfo.getObject());
	}
	
	public boolean objectCanDeactivate(Transaction transaction, ObjectInfo objectInfo) {
		return EventPlatform.triggerCancellableObjectEventArgs(transaction, _deactivating, objectInfo, objectInfo.getObject());
	}
	
	public void objectOnActivate(Transaction transaction, ObjectInfo obj) {
		EventPlatform.triggerObjectInfoEvent(transaction, _activated, obj);
	}
	
	public void objectOnNew(Transaction transaction, ObjectInfo obj) {
		EventPlatform.triggerObjectInfoEvent(transaction, _created, obj);
	}
	
	public void objectOnUpdate(Transaction transaction, ObjectInfo obj) {
		EventPlatform.triggerObjectInfoEvent(transaction, _updated, obj);
	}
	
	public void objectOnDelete(Transaction transaction, ObjectInfo obj) {
		EventPlatform.triggerObjectInfoEvent(transaction, _deleted, obj);		
	}	

	public void classOnRegistered(ClassMetadata clazz) {
		EventPlatform.triggerClassEvent(_classRegistered, clazz);		
	}	

	public void objectOnDeactivate(Transaction transaction, ObjectInfo obj) {
		EventPlatform.triggerObjectInfoEvent(transaction, _deactivated, obj);
	}
	
	public void objectOnInstantiate(Transaction transaction, ObjectInfo obj) {
		EventPlatform.triggerObjectInfoEvent(transaction, _instantiated, obj);
	}
	
	public void commitOnStarted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections) {
		EventPlatform.triggerCommitEvent(transaction, _committing, objectInfoCollections);
	}
	
	public void commitOnCompleted(Transaction transaction, CallbackObjectInfoCollections objectInfoCollections) {
		EventPlatform.triggerCommitEvent(transaction, _committed, objectInfoCollections);
	}
	
	public void closeOnStarted(ObjectContainer container) {
		EventPlatform.triggerObjectContainerEvent(container, _closing);
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
	
	public Event4 closing() {
		return _closing;
	}
	
	protected void onCommittedListener() {
		// TODO: notify the server that we are interested in 
		// committed callbacks
		_container.onCommittedListener();
	}

	public boolean caresAboutCommitting() {
		return EventPlatform.hasListeners(_committing);
	}

	public boolean caresAboutCommitted() {
		return EventPlatform.hasListeners(_committed);
	}
	
    public boolean caresAboutDeleting() {
        return EventPlatform.hasListeners(_deleting);
    }

    public boolean caresAboutDeleted() {
        return EventPlatform.hasListeners(_deleted);
    }	
}
