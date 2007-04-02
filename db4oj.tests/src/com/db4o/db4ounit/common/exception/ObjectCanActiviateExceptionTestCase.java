/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exception;

import com.db4o.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ObjectCanActiviateExceptionTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new ObjectCanActiviateExceptionTestCase().runSoloAndClientServer();
	}

	public static class Item {
		public boolean objectCanActivate(ObjectContainer container) {
			throw new ItemException();
		}
	}

	public void test() {
		store(new Item());
		Assert.expect(ReflectException.class, ItemException.class,
				new CodeBlock() {
					public void run() throws Throwable {
						ObjectSet os = db().get(null);
						os.next();
					}
				});
	}
}
