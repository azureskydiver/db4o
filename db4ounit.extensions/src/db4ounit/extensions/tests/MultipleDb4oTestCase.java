/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.tests;

import com.db4o.config.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class MultipleDb4oTestCase extends AbstractDb4oTestCase {
	private static int configureCalls=0;
	
	public static void resetConfigureCalls() {
		configureCalls=0;
	}
	
	public static int configureCalls() {
		return configureCalls;
	}

	protected void configure(Configuration config) {
		configureCalls++;
	}
	
	public void testFirst() {
		Assert.fail();
	}

	public void testSecond() {
		Assert.fail();
	}
}
