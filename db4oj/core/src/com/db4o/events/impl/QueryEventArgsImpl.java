/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events.impl;

import com.db4o.events.QueryEventArgs;
import com.db4o.query.Query;

/**
 * @exclude
 */
public class QueryEventArgsImpl implements QueryEventArgs {
	
	private Query _query;

	public QueryEventArgsImpl(Query subject) {
		_query = subject;
	}

	public Query query() {
		return _query;
	}
	
	public Object object() {
		return _query;
	}
}
