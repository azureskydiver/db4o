/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DatabaseClosedExceptionTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new DatabaseClosedExceptionTestCase().runAll();
	}
	
	public void test() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().get(null);
			}
		});
		
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().set(new Item());
			}
		});
		
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().commit();
			}
		});
		
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().rollback();
			}
		});
	}
	
}
