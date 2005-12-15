package com.db4o.inside.query;


public interface Db4oQueryExecutionListener {
	void notifyQueryExecuted(NQOptimizationInfo info);
}
