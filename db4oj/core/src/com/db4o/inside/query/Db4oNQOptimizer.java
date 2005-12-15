package com.db4o.inside.query;

import com.db4o.query.*;

public interface Db4oNQOptimizer {
	Object optimize(Query query, Predicate filter);
}
