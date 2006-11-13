/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.defrag;

import com.db4o.db4ounit.common.defragment.*;

public class RunTestsDefrag extends AbstractDb4oDefragTestCase {
	
	@Override
	public Class testSuite() {
		return com.db4o.db4ounit.jre5.AllTestsDb4oUnitJdk5.class;
	}

	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}
}
