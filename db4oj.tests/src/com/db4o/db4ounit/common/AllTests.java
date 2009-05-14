/* Copyright (C) 2006   Versant Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common;

import db4ounit.extensions.*;

/**
 * 
 */
public class AllTests extends ComposibleTestSuite {
	
	/**
	 * @sharpen.ignore test suited is executed differently under .net
	 */
	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}

	protected Class[] testCases() {
		return composeTests(
				new Class[] {
						com.db4o.db4ounit.common.acid.AllTests.class,
						com.db4o.db4ounit.common.activation.AllTests.class,
						com.db4o.db4ounit.common.api.AllTests.class,
						com.db4o.db4ounit.common.assorted.AllTests.class,
						com.db4o.db4ounit.common.backup.AllTests.class,
						com.db4o.db4ounit.common.btree.AllTests.class,
						com.db4o.db4ounit.common.classindex.AllTests.class,
						com.db4o.db4ounit.common.config.AllTests.class,
						com.db4o.db4ounit.common.constraints.AllTests.class,
						com.db4o.db4ounit.common.defragment.AllTests.class,
						com.db4o.db4ounit.common.diagnostics.AllTests.class,
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
						com.db4o.db4ounit.common.refactor.AllTests.class,
						com.db4o.db4ounit.common.references.AllTests.class,
						com.db4o.db4ounit.common.reflect.AllTests.class,
						com.db4o.db4ounit.common.regression.AllTests.class,
						com.db4o.db4ounit.common.querying.AllTests.class,
						com.db4o.db4ounit.common.set.AllTests.class,
						com.db4o.db4ounit.common.soda.AllTests.class,
						com.db4o.db4ounit.common.stored.AllTests.class,
						com.db4o.db4ounit.common.ta.AllCommonTATests.class,
						com.db4o.db4ounit.common.tp.AllTests.class,
						com.db4o.db4ounit.common.types.AllTests.class,
						com.db4o.db4ounit.common.uuid.AllTests.class,
						com.db4o.db4ounit.optional.AllTests.class,
						com.db4o.db4ounit.util.test.AllTests.class,
						});
	}
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	@Override
	protected Class[] composeWith() {
		return new Class[] {
				com.db4o.db4ounit.common.cs.AllTests.class, 
				};
	}
}
