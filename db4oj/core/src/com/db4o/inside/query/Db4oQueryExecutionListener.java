package com.db4o.inside.query;

import com.db4o.query.*;

public interface Db4oQueryExecutionListener {
	void notifyQueryExecuted(Predicate filter,String msg);
}
