/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ArrayListTypeHandlerTestCase implements TestSuiteBuilder {

	public Iterator4 iterator() {
		return new Db4oTestSuiteBuilder(
				new Db4oInMemory(),
				ArrayListTypeHandlerTestSuite.class).iterator();
	}

}
