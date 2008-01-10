/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InvalidIDExceptionTestCase extends AbstractDb4oTestCase {
	public static void main(String[] args) {
		new InvalidIDExceptionTestCase().runAll();
	}

	public void testBigId() {
		Item item = new Item();
		store(item);
		final long id = db().getID(item);
		Assert.expect(InvalidIDException.class, InvalidIDException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().getByID(id + 10000000);
			}
			
		});
		
//		Assert.expect(InvalidIDException.class, new CodeBlock() {
//			public void run() throws Throwable {
//				db().getByID(id + 1);
//			}
//			
//		});
	}
	
	public void testSmallId() throws Exception {
		Assert.expect(InvalidIDException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().getByID(1000);
			}			
		});
	}

}
