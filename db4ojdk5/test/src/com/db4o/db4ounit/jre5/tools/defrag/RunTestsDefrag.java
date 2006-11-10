/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.tools.defrag;

import com.db4o.db4ounit.jre11.tools.defragment.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class RunTestsDefrag {
	public static void main(String[] args) {
		Class clazz = 
			com.db4o.db4ounit.jre5.AllTestsDb4oUnitJdk5.class;
//			PCollectionReferencedTwiceTestCase.class;
//			PersistStaticFieldValuesTestCase.class;
		System.exit(new TestRunner(
				new Db4oTestSuiteBuilder(
						new Db4oDefragSolo(new IndependentConfigurationSource()), clazz)).run());
	}
}
