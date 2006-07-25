package com.db4o.events.impl;

import com.db4o.events.QueryEventArgs;
import com.db4o.query.Query;

public class QueryEventArgsImpl implements QueryEventArgs {
	
	private Query _subject;

	public QueryEventArgsImpl(Query subject) {
		_subject = subject;
	}

	public Query subject() {
		return _subject;
	}

}
