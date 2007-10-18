/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

/**
 * @exclude
 */
import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.extensions.*;

public class TransparentActivationTestCaseBase extends AbstractDb4oTestCase  {

	public TransparentActivationTestCaseBase() {
		super();
	}

	protected void configure(Configuration config) {
		config.add(new TransparentActivationSupport());
	}

}