/* Copyright (C) 2004 - 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.cs.common.util.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class IsStoredTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new IsStoredTestCase().runConcurrency();
	}

	String myString;

	public void conc(ExtObjectContainer oc) {
		IsStoredTestCase isStored = new IsStoredTestCase();
		isStored.myString = "isStored";
		oc.set(isStored);
		Assert.isTrue(oc.isStored(isStored));
		Db4oUtil.assertOccurrences(oc, IsStoredTestCase.class, 1);
		oc.delete(isStored);
		Assert.isFalse(oc.isStored(isStored));
		oc.rollback();
		Assert.isTrue(oc.isStored(isStored));
		oc.delete(isStored);
		Assert.isFalse(oc.isStored(isStored));
		Db4oUtil.assertOccurrences(oc, IsStoredTestCase.class, 0);
		oc.commit();
		Assert.isFalse(oc.isStored(isStored));
	}

	public void check(ExtObjectContainer oc) {
		assertOccurrences(oc, IsStoredTestCase.class, 0);
	}

}
