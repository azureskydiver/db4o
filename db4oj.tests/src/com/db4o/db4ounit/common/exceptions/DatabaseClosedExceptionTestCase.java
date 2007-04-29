/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DatabaseClosedExceptionTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new DatabaseClosedExceptionTestCase().runAll();
	}

	public void testRollback() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().rollback();
			}
		});
	}

	public void testCommit() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().commit();
			}
		});
	}

	public void testSet() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().set(new Item());
			}
		});
	}

	public void testDelete() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().delete(null);
			}
		});
	}

	public void testQueryClass() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().query(this.getClass());
			}
		});
	}

	public void testQuery() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().query();
			}
		});
	}

	public void testDeactivate() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().deactivate(null, 1);
			}
		});
	}

	public void testActivate() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().activate(null, 1);
			}
		});
	}

	public void testGet() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().get(null);
			}
		});
	}

}
