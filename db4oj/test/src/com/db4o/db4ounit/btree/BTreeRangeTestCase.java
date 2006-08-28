package com.db4o.db4ounit.btree;

import com.db4o.inside.btree.*;

public class BTreeRangeTestCase extends BTreeTestCaseBase {
	
	public static void main(String[] args) {
		new BTreeRangeTestCase().runSolo();
	}
	
	private BTree _btree;

	public void setUp() throws Exception {
		super.setUp();
		
		_btree = createIntKeyBTree(0);		
		add(new int[] { 3, 7, 4, 9 });
		
	}	

	public void _testIntersect() {
		final BTreeRange range1 = createIncludingRange(3, 7);
		assertRange(new int[] { 3, 4, 7 }, range1);
		
		final BTreeRange range2 = createIncludingRange(4, 9);
		assertRange(new int[] { 4, 7, 9 }, range2);
		
		final BTreeRange intersection = range1.intersect(range2);		
		assertRange(new int[] { 4, 7 }, intersection);
	}

	private BTreeRange createIncludingRange(int lower, int upper) {
		final BTreeRange lowerRange = search(lower);
		final BTreeRange upperRange = search(upper);
		return lowerRange.extendToLast(upperRange);
	}
	
	private void assertRange(int[] expectedKeys, BTreeRange intersection) {
		final ExpectingVisitor visitor = createExpectingVisitor(expectedKeys);
		traverseKeys(intersection, visitor);
		visitor.assertExpectations();
	}

	private BTreeRange search(int key) {
		return _btree.search(trans(), new Integer(key));
	}
	
	private void add(int[] keys) {
		for (int i=0; i<keys.length; ++i) {
			_btree.add(trans(), new Integer(keys[i]));
		}
	}
}
