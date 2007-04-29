/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DatabaseReadonlyExceptionTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new DatabaseReadonlyExceptionTestCase().runAll();
	}

	public void testRollback() {
		configReadOnly();
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().rollback();
			}
		});
	}

	public void testCommit() {
		configReadOnly();
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().commit();
			}
		});
	}

	public void testSet() {
		configReadOnly();
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().set(new Item());
			}
		});
	}

	public void testDelete() {
		configReadOnly();
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().delete(null);
			}
		});
	}

	private void configReadOnly() {
		db().configure().readOnly(true);
	}
}
