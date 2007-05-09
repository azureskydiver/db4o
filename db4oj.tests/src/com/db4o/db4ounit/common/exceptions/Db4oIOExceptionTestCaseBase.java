/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.config.*;
import com.db4o.foundation.io.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class Db4oIOExceptionTestCaseBase extends AbstractDb4oTestCase implements
		OptOutCS {
	
	protected void configure(Configuration config) {
		config.lockDatabaseFile(false);
		config.io(new ExceptionIOAdapter());
	}
	
	protected void db4oSetupBeforeStore() throws Exception {
		ExceptionIOAdapter.exception = false;
	}

	protected void db4oCustomTearDown() throws Exception {
		ExceptionIOAdapter.exception = false;
	}
}
