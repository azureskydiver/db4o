/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ta;

import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class TransparentActivationSupportTestCase extends AbstractDb4oTestCase implements OptOutCS {

	protected void configure(Configuration config) {
		config.add(new TransparentActivationSupport());
	}
	
	public void testActivationDepth() {
		Assert.areEqual(0, db().configure().activationDepth());
	}
}
