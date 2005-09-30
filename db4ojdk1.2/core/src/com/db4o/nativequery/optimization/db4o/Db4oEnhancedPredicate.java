package com.db4o.nativequery.optimization.db4o;

import com.db4o.query.*;

public interface Db4oEnhancedPredicate {
	void optimizeQuery(Query query);
}
