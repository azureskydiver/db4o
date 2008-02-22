/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures.dynamic;

import db4ounit.*;

public interface TestDecorator {
	
	Test decorate(Test test);

}
