/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.osgi.test;

public interface Db4oTestService {

	int runTests(String databaseFilePath) throws Exception;
	
}
