/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.events;

import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * Arguments for commit time related events.
 * 
 * @see EventRegistry
 */
public class CommitEventArgs extends TransactionalEventArgs {
	
	private final CallbackObjectInfoCollections _collections;

	public CommitEventArgs(Transaction transaction, CallbackObjectInfoCollections collections) {
		super(transaction);
		_collections = collections;
	}
	
	/**
	 * Returns a iteration
	 * 
	 * @sharpen.property
	 */
	public ObjectInfoCollection added() {
		return _collections.added;
	}
	
	/**
	 * @sharpen.property
	 */
	public ObjectInfoCollection deleted() {
		return _collections.deleted;
	}

	/**
	 * @sharpen.property
	 */
	public ObjectInfoCollection updated() {
		return _collections.updated;
	}
}
