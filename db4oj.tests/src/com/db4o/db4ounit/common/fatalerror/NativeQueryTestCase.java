/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.fatalerror;

import com.db4o.ObjectSet;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.query.Predicate;

import db4ounit.Assert;
import db4ounit.CodeBlock;
import db4ounit.extensions.AbstractDb4oTestCase;

public class NativeQueryTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new NativeQueryTestCase().runSoloAndClientServer();
	}

	protected void store() throws Exception {
		store(new Item("hello"));
	}

	public void _test() {
		Assert.expect(NQError.class, new CodeBlock() {
			public void run() throws Exception {
				Predicate fatalErrorPredicate = new FatalErrorPredicate();
				db().query(fatalErrorPredicate);
			}
		});
		Assert.isTrue(db().isClosed());
	}

	public static class FatalErrorPredicate extends Predicate {
		public boolean match(Object item) {
			throw new NQError("nq error!");
		}
	}
	
	public static class NQError extends Error {
		public NQError(String msg) {
			super(msg);
		}
	}
}
