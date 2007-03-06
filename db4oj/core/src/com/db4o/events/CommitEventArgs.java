/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.events;

import com.db4o.ext.ObjectInfo;

/**
 * Arguments for commit time related events.
 * 
 * @see EventRegistry
 */
public class CommitEventArgs extends EventArgs {
	
	private ObjectInfo[] _added;

	public CommitEventArgs(ObjectInfo[] added) {
		_added = added;
	}

	/**
	 * @sharpen.property
	 */
	public ObjectInfo[] added() {
		return _added;
	}
}
