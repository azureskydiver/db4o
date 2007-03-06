package com.db4o.db4ounit.util.test;

import com.db4o.db4ounit.util.*;

import db4ounit.*;

public class PermutingTestConfigTestCase implements TestCase {

	public void testPermutation() {
		Object[][] data={
				{"A","B"},
				{"X","Y","Z"},
		};
		final PermutingTestConfig config=new PermutingTestConfig(data);
		Object[][] expected={
				{"A","X"},	
				{"A","Y"},	
				{"A","Z"},	
				{"B","X"},	
				{"B","Y"},	
				{"B","Z"},	
		};
		for (int groupIdx = 0; groupIdx < expected.length; groupIdx++) {
			Assert.isTrue(config.moveNext());
			Object[] current={config.current(0),config.current(1)};
			ArrayAssert.areEqual(expected[groupIdx],current);
		}
		Assert.isFalse(config.moveNext());
	}
	
}
