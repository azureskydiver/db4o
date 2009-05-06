/* Copyright (C) 2006   Versant Inc.   http://www.db4o.com */

package com.db4o.events;

import com.db4o.internal.*;
import com.db4o.query.*;

/**
 * Arguments for {@link Query} related events.
 * 
 * @see EventRegistry
 */
public class QueryEventArgs extends ObjectEventArgs {
	
	/**
	 * Creates a new instance for the specified {@link Query} instance.
	 */
	public QueryEventArgs(Transaction transaction, Query q) {
		super(transaction, q);
	}
	
	/**
	 * The {@link Query} which triggered the event.
	 * 
	 * @sharpen.property
	 */
	public Query query() {
		return (Query)object();
	}
}
