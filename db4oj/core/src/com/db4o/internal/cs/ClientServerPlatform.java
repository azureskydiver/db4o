/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.Iterator4;
import com.db4o.internal.query.result.AbstractQueryResult;

/**
 * Platform specific defaults.
 */
public class ClientServerPlatform {

	/**
	 * The default {@link ClientQueryResultIterator} for this platform.
	 * 
	 * @return
	 */
	public static Iterator4 createClientQueryResultIterator(AbstractQueryResult result) {
		final QueryResultIteratorFactory factory = result.config().queryResultIteratorFactory();
		if (null != factory) return factory.newInstance(result);
		return new ClientQueryResultIterator(result);
	}

}
