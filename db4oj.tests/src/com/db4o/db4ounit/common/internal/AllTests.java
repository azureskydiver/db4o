/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.internal.marshall.*;

import db4ounit.extensions.*;


public class AllTests extends Db4oTestSuite {
    
    public static void main(String[] args) {
        new AllTests().runSolo();
    }
	
	protected Class[] testCases() {
		return new Class[] {
			Comparable4TestCase.class,
			DeactivateTestCase.class,
		    EmbeddedClientObjectContainerTestCase.class,
		    InternalObjectContainerAPITestCase.class,
		    MarshallerFamilyTestCase.class,
		    MarshallingBufferTestCase.class,
		    MarshallingContextTestCase.class,
		    PartialObjectContainerTestCase.class,
			SerializerTestCase.class,
			TransactionLocalTestCase.class,
			TransactionTestCase.class,
		};
	}

}
