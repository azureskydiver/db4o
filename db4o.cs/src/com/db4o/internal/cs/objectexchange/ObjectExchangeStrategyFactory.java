/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.cs.objectexchange;

import com.db4o.foundation.*;

public class ObjectExchangeStrategyFactory {

	public static ObjectExchangeStrategy forConfig(final ObjectExchangeConfiguration config) {
		if (config.prefetchDepth > 1) {
			throw new NotImplementedException("config.prefetchDepth must be either 0 or 1");
		}
        if (config.prefetchDepth > 0) {
    		return new EagerObjectExchangeStrategy(config);
    	}
    	return DeferredObjectExchangeStrategy.INSTANCE;
    }

}
