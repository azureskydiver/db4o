package com.db4o.inside.query;

import com.db4o.query.*;

public interface Db4oNQOptimizer {
	void optimize(Query query,Predicate filter);
}
