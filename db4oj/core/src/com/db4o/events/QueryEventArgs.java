/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events;

import com.db4o.query.Query;

/**
 * Arguments for {@link Query} related events.
 * 
 * @see EventRegistry
 */
public class QueryEventArgs extends ObjectEventArgs {
	
	/**
	 * Creates a new instance for the specified {@link Query} instance.
	 */
	public QueryEventArgs(Query q) {
		super(q);
	}
	
	/**
	 * The {@link Query} which triggered the event.
	 * 
	 * @property
	 */
	public Query query() {
		return (Query)object();
	}
}
