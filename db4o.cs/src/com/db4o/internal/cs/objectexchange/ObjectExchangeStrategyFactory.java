/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.objectexchange;

public class ObjectExchangeStrategyFactory {

	public static ObjectExchangeStrategy forPrefetchDepth(final int prefetchDepth) {
        if (prefetchDepth > 0) {
    		return new EagerObjectExchangeStrategy(prefetchDepth);
    	}
    	return DeferredObjectExchangeStrategy.INSTANCE;
    }

}
