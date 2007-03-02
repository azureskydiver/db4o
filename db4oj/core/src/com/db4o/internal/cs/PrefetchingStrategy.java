/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.IntIterator4;


/**
 * Defines a strategy on how to prefetch objects from the server.
 */
public interface PrefetchingStrategy {

	int prefetchObjects(ClientObjectContainer container, IntIterator4 ids,
			Object[] prefetched, int prefetchCount);

}
