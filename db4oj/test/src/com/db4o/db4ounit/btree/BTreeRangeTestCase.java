package com.db4o.db4ounit.btree;

import com.db4o.inside.btree.*;

import db4ounit.Assert;

public class BTreeRangeTestCase extends BTreeTestCaseBase {
	
	public static void main(String[] args) {
		new BTreeRangeTestCase().runSolo();
	}
	
	public void setUp() throws Exception {
		super.setUp();
		
		_btree = BTreeAssert.createIntKeyBTree(stream(), 0);		
		add(new int[] { 3, 7, 4, 9 });
		
	}	

	public void testIntersect() {
		
		assertIntersection(new int[] { 4, 7 }, range(3, 7), range(4, 9));		
		assertIntersection(new int[] {}, range(3, 4), range(7, 9));		
		assertIntersection(new int[] { 3, 4, 7, 9 }, range(3, 9), range(3, 9));		
		assertIntersection(new int[] { 3, 4, 7, 9 }, range(3, 10), range(3, 9));		
		assertIntersection(new int[] {}, range(1, 2), range(3, 9));
		
	}
	
	public void _testUnion() {
		
		assertUnion(new int[] { 3, 4, 7, 9 }, range(3, 4), range(7, 9));
		assertUnion(new int[] { 3, 4, 7, 9 }, range(3, 7), range(4, 9));
		assertUnion(new int[] { 3, 7, 9 }, range(3, 3), range(7, 9));
		assertUnion(new int[] { 3, 9 }, range(3, 3), range(9, 9));
		
	}
	
	public void testOverlaps() {
		
		assertOverlaps(range(3, 4), range(4, 9));
		
		Assert.isFalse(range(3, 4).overlaps(range(7, 9)));

	}

	private void assertOverlaps(BTreeRange range1, BTreeRange range2) {
		Assert.isTrue(range1.overlaps(range2));
		Assert.isTrue(range2.overlaps(range1));
	}

	private void assertUnion(int[] expectedKeys, BTreeRange range1, BTreeRange range2) {
		BTreeAssert.assertRange(expectedKeys, range1.union(range2));
		BTreeAssert.assertRange(expectedKeys, range2.union(range1));
	}

	private void assertIntersection(int[] expectedKeys, BTreeRange range1, BTreeRange range2) {
		BTreeAssert.assertRange(expectedKeys, range1.intersect(range2));
		BTreeAssert.assertRange(expectedKeys, range2.intersect(range1));
	}
	
	public void testExtendToLastOf() {
		BTreeAssert.assertRange(new int[] { 3, 4, 7 }, range(3, 7));		
		BTreeAssert.assertRange(new int[] { 4, 7, 9 }, range(4, 9));
	}
}
