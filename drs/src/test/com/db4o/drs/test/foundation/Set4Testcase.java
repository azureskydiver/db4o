package com.db4o.drs.test.foundation;

import com.db4o.foundation.Iterators;

import db4ounit.Assert;
import db4ounit.TestCase;

public class Set4Testcase implements TestCase {
	
	public void testSingleElementIteration() {
		Set4 set = newSet("first");
		Assert.areEqual("first", Iterators.next(set.iterator()));
	}

	public void testContainsAll() {
		Set4 set = newSet("42");
		set.add("foo");
		
		Assert.isTrue(set.containsAll(newSet("42")));
		Assert.isTrue(set.containsAll(newSet("foo")));
		Assert.isTrue(set.containsAll(set));
		
		Set4 other = new Set4(set);
		other.add("bar");
		Assert.isFalse(set.containsAll(other));
	}
	
	private Set4 newSet(final String firstElement) {
		Set4 set = new Set4();
		set.add(firstElement);
		return set;
	}

}
