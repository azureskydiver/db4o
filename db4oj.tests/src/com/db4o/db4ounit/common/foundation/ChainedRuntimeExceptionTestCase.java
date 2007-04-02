/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.foundation;

import com.db4o.ext.*;

import db4ounit.*;

public class ChainedRuntimeExceptionTestCase implements TestCase {

	public static void main(String[] args) {
		new TestRunner(ChainedRuntimeExceptionTestCase.class).run();
	}

	static class CauseException extends Exception {

	}

	public void testPrintStackTrace() {
		CauseException cause = new CauseException();
		Db4oException e = new Db4oException(cause);
		e.printStackTrace();

	}
}
