/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

@decaf.Ignore
public interface QueriesMBean {
	
	double getClassIndexScansPerSecond();
	
	double getAverageQueryExecutionTime();
	double getQueriesPerSecond();
	

}
