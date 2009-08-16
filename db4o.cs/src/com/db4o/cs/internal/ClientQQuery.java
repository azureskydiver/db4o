/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.cs.internal;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;

/**
 * @exclude
 */
public final class ClientQQuery extends QQuery {
	public ClientQQuery() {
	}
	
	public ClientQQuery(Transaction aTrans, QQuery aParent, String aField) {
		super(aTrans, aParent, aField);
	}

	@Override
	protected QueryResult executeQuery() {
		return triggeringQueryEvents(new Closure4<QueryResult>() { public QueryResult run() {
			return executeQueryImpl();
		}});
	}
}