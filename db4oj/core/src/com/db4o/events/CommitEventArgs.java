/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.events;

import com.db4o.ext.ObjectInfoCollection;

/**
 * Arguments for commit time related events.
 * 
 * @see EventRegistry
 */
public class CommitEventArgs extends EventArgs {
	
	private final ObjectInfoCollection _added;
	private final ObjectInfoCollection _deleted;
	private final ObjectInfoCollection _updated;

	public CommitEventArgs(ObjectInfoCollection added, ObjectInfoCollection deleted, ObjectInfoCollection updated) {
		_added = added;
		_deleted = deleted;
		_updated = updated;
	}

	/**
	 * Returns a iteration
	 * 
	 * @sharpen.property
	 */
	public ObjectInfoCollection added() {
		return _added;
	}
	
	/**
	 * @sharpen.property
	 */
	public ObjectInfoCollection deleted() {
		return _deleted;
	}

	/**
	 * @sharpen.property
	 */
	public ObjectInfoCollection updated() {
		return _updated;
	}
}
