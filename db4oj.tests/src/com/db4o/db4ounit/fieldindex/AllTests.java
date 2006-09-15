/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.inside.marshall.*;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		Class[] fieldBased={
				IndexedNodeTestCase.class,
	            FieldIndexTestCase.class,
	            FieldIndexProcessorTestCase.class,
		};
		Class[] neutral={
	            StringIndexTestCase.class,
		};
		Class[] tests=neutral;
        if(MarshallerFamily.BTREE_FIELD_INDEX ){
        	tests=new Class[fieldBased.length+neutral.length];
        	System.arraycopy(neutral, 0, tests, 0, neutral.length);
        	System.arraycopy(fieldBased, 0, tests, neutral.length, fieldBased.length);
        }
        return tests;
    }
}
