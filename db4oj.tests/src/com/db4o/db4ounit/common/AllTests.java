/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common;

import db4ounit.extensions.Db4oTestSuite;

/**
 * 
 */
public class AllTests extends Db4oTestSuite {
	
	/**
	 * @sharpen.ignore test suited is executed differently under .net
	 */
	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}

	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.common.acid.AllTests.class,
			com.db4o.db4ounit.common.assorted.AllTests.class,
            com.db4o.db4ounit.common.btree.AllTests.class,
            com.db4o.db4ounit.common.classindex.AllTests.class,
            com.db4o.db4ounit.common.config.AllTests.class,
            com.db4o.db4ounit.common.constraints.AllTests.class,
            com.db4o.db4ounit.common.cs.AllTests.class,
			com.db4o.db4ounit.common.defragment.AllTests.class,
			com.db4o.db4ounit.common.events.AllTests.class,
			com.db4o.db4ounit.common.exceptions.AllTests.class,
			com.db4o.db4ounit.common.ext.AllTests.class,
            com.db4o.db4ounit.common.fatalerror.AllTests.class,
            com.db4o.db4ounit.common.fieldindex.AllTests.class,
            com.db4o.db4ounit.common.foundation.AllTests.class,
            com.db4o.db4ounit.common.freespace.AllTests.class,
			com.db4o.db4ounit.common.handlers.AllTests.class,
			com.db4o.db4ounit.common.header.AllTests.class,
			com.db4o.db4ounit.common.interfaces.AllTests.class,
			com.db4o.db4ounit.common.internal.AllTests.class,
			com.db4o.db4ounit.common.io.AllTests.class,
			com.db4o.db4ounit.common.reflect.AllTests.class,
			com.db4o.db4ounit.common.regression.AllTests.class,
			com.db4o.db4ounit.common.querying.AllTests.class,
			com.db4o.db4ounit.common.set.AllTests.class,
			com.db4o.db4ounit.common.soda.AllTests.class,
			com.db4o.db4ounit.common.stored.AllTests.class,
			com.db4o.db4ounit.common.types.AllTests.class,
			com.db4o.db4ounit.util.test.AllTests.class,
		};
	}
}
