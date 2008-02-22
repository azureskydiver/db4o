/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DatabaseReadonlyExceptionTestCase
	extends AbstractDb4oTestCase
	implements OptOutTA {

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
				db().store(new Item());
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
	
	public void testNewFile() {
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				fixture().close();
				fixture().clean();
				fixture().config().readOnly(true);
				fixture().open(getClass());
			}
		});
	}

	public void testReserveStorage() {
	    configReadOnly();
		Class exceptionType = isClientServer() && ! isMTOC() ? NotSupportedException.class
				: DatabaseReadOnlyException.class;
		Assert.expect(exceptionType, new CodeBlock() {
			public void run() throws Throwable {
				db().configure().reserveStorageSpace(1);
			}
		});
	}
	
	public void testStoredClasses() {
	    configReadOnly();
	    db().storedClasses();
	}
	
	private void configReadOnly() {
		db().configure().readOnly(true);
	}
	
}
