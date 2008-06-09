/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
    
    public static void main(String[] arguments) {
        new AllTests().runAll();
    }
    
	protected Class[] testCases() {
		return new Class[] {
		    ArrayListCandidatesTestCase.class,
		    CollectionActivationTestCase.class,
			CollectionUuidTest.class,
			EmbeddedClientObjectContainerJre12TestCase.class,
			HashMapUpdateFileSizeTestCase.class,
			Iterator4JdkIteratorTestCase.class,
			JdkCollectionIterator4TestCase.class,
			NestedListTestCase.class,
			NoP1ObjectIndexTestCase.class,
			PersistentListTestCase.class,
			SetCollectionOnUpdateTestCase.class,
			com.db4o.db4ounit.jre12.collections.custom.AllTests.class,
			com.db4o.db4ounit.jre12.collections.map.AllTests.class,
		};
	}

}
