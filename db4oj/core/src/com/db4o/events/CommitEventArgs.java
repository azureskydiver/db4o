/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.events;

import com.db4o.ext.ObjectInfoCollection;
import com.db4o.internal.*;

/**
 * Arguments for commit time related events.
 * 
 * @see EventRegistry
 */
public class CommitEventArgs extends EventArgs {
	
	private final CallbackObjectInfoCollections _collections;
	private final Object _transaction;

	public CommitEventArgs(Object transaction, CallbackObjectInfoCollections collections) {
		_transaction = transaction;
		_collections = collections;
	}
	
	/**
	 * The transaction being committed.
	 * 
	 * @sharpen.property
	 */
	public Object transaction() {
		return _transaction;
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
