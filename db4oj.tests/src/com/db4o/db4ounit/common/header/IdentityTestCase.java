/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.header;

import com.db4o.YapFile;
import com.db4o.ext.Db4oDatabase;

import db4ounit.Assert;
import db4ounit.ArrayAssert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.OptOutCS;

public class IdentityTestCase extends AbstractDb4oTestCase implements OptOutCS {

	public static void main(String[] arguments) {
		new IdentityTestCase().runSolo();
	}

	public void testIdentityPreserved() throws Exception {

		Db4oDatabase ident = db().identity();

		reopen();

		Db4oDatabase ident2 = db().identity();

		Assert.isNotNull(ident);
		Assert.areEqual(ident, ident2);
	}

	public void testGenerateIdentity() throws Exception {

		byte[] oldSignature = db().identity().getSignature();

		generateNewIdentity();

		reopen();

		ArrayAssert.areNotEqual(oldSignature, db().identity().getSignature());
	}

	private void generateNewIdentity() {
		((YapFile) db()).generateNewIdentity();
	}
}
