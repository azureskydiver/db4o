package com.db4o.drs.test.foundation;

import com.db4o.foundation.Iterators;

import db4ounit.Assert;
import db4ounit.TestCase;

public class Set4Testcase implements TestCase {
	
	public void testSingleElementIteration() {
		Set4 set = new Set4();
		set.add("first");
		Assert.areEqual("first", Iterators.next(set.iterator()));
		
	}

}
