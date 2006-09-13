/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import db4ounit.extensions.Db4oTestSuite;

/**
 * @exclude
 */
public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] { CollectionFieldIndexTestCase.class };
	}

}
