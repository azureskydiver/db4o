/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.tests;

import db4ounit.Assert;
import db4ounit.extensions.Db4oTestCase;

public class MultipleDb4oTestCase extends Db4oTestCase {
	public void testFirst() {
		Assert.fail();
	}

	public void testSecond() {
		Assert.fail();
	}
}
