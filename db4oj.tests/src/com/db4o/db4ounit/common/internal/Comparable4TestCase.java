/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;

import db4ounit.*;

public class Comparable4TestCase implements TestCase {

	public static void main(String[] args) {
		new TestRunner(Comparable4TestCase.class).run();
	}
	
	public void testIntHandler(){
		Assert.isGreater(0, compareInteger(4, 2));
		Assert.isSmaller(0, compareInteger(3, 5));
		Assert.areEqual(0, compareInteger(7, 7));
		// Assert.expect(NullPointerExcep, block)
	}

	private int compareInteger(int lhs, int rhs) {
		IntHandler intHandler = new IntHandler(null);
		PreparedComparison comparable = intHandler.newPrepareCompare(new Integer(lhs));
		return comparable.compareTo(new Integer(rhs));
	}

}
