/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.inside.marshall.*;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
        if(! MarshallerFamily.BTREE_FIELD_INDEX ){
            return new Class[]{};
        }
		return  new Class[] {
			IndexedNodeTestCase.class,
            FieldIndexTestCase.class,
            FieldIndexProcessorTestCase.class,
		};
    }
}
