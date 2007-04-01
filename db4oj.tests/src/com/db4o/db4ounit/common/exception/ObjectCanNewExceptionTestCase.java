/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exception;

import com.db4o.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ObjectCanNewExceptionTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new ObjectCanNewExceptionTestCase().runSoloAndClientServer();
	}

	public static class Item {
		public boolean objectCanNew(ObjectContainer container) {
			throw new ItemException();
		}
	}

	public void test() {
		Assert.expect(Db4oUserException.class, ItemException.class,
				new CodeBlock() {
					public void run() throws Throwable {
						store(new Item());
					}
				});
	}
}
