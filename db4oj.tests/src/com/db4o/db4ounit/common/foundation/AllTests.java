/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;


import com.db4o.foundation.*;

import db4ounit.ReflectionTestSuiteBuilder;
import db4ounit.TestRunner;
import db4ounit.TestSuiteBuilder;


public class AllTests implements TestSuiteBuilder {
	
	public Iterator4 build() {
		return new ReflectionTestSuiteBuilder(new Class[] {
			Algorithms4TestCase.class,
			ArrayIterator4TestCase.class,
			Arrays4TestCase.class,
			BitMap4TestCase.class,
			BlockingQueueTestCase.class,
			Collection4TestCase.class,
			CompositeIterator4TestCase.class,
			ContextVariableTestCase.class,
			CoolTestCase.class,
			Hashtable4TestCase.class,
			IntArrayListTestCase.class,
			IntMatcherTestCase.class,
			Iterable4AdaptorTestCase.class,
			IteratorsTestCase.class,
			NoDuplicatesQueueTestCase.class,
			NonblockingQueueTestCase.class,
			Path4TestCase.class,
			SortedCollection4TestCase.class,
			Stack4TestCase.class,
			TreeKeyIteratorTestCase.class,
			TreeNodeIteratorTestCase.class,
			BufferTestCase.class,
		}).build();	
	}
	
	public static void main(String[] args) {
		new TestRunner(AllTests.class).run();
	}

}
