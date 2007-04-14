package com.db4o.ta.test;

import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransparentActivationSupportTestCase extends AbstractDb4oTestCase {

	protected void configure(Configuration config) {
		config.add(new TransparentActivationSupport());
	}
	
	public void testActivationDepth() {
		Assert.areEqual(0, db().configure().activationDepth());
	}
}
