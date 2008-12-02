/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import com.db4o.db4ounit.common.util.*;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		Class[] commonCases = new Class[] {
				BlockSizeDefragTestCase.class,
				DefragInheritedFieldIndexTestCase.class,
				SlotDefragmentTestCase.class,
				StoredClassFilterTestCase.class,
				TranslatedDefragTestCase.class,
		};
		return Db4oUnitTestUtil.mergeClasses(commonCases, nonDecafTestCases());
	}

	/**
	 * @decaf.replaceFirst return new Class[0];
	 */
	private Class[] nonDecafTestCases() {
		return new Class[] {
				DefragEncryptedFileTestCase.class,
		};
	}

}
