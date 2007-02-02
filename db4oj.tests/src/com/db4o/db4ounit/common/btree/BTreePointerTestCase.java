/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

import com.db4o.db4ounit.common.foundation.IntArrays4;
import com.db4o.foundation.Iterator4;
import com.db4o.internal.btree.*;

import db4ounit.Assert;

/**
 * @exclude
 */
public class BTreePointerTestCase extends BTreeTestCaseBase {
	
	public static void main(String[] args) {
		new BTreePointerTestCase().runSolo();
	}

	private final int[] keys = new int[] {
			-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 7, 9
	};
	
	protected void db4oSetupAfterStore() throws Exception {
		super.db4oSetupAfterStore();
		add(keys);		
		commit();
	}
	
	public void testLastPointer(){
		BTreePointer pointer = _btree.lastPointer(trans());
		assertPointerKey(9, pointer);
	}
	
	public void testPrevious(){
		BTreePointer pointer = getPointerForKey(3);
		BTreePointer previousPointer = pointer.previous();
		assertPointerKey(2, previousPointer);
	}

	public void testNextOperatesInReadMode() {				
		BTreePointer pointer = _btree.firstPointer(trans());		
		assertReadModePointerIteration(keys, pointer);
	}	
	
	public void testSearchOperatesInReadMode() {
		final BTreePointer pointer = getPointerForKey(3);
		assertReadModePointerIteration(
				new int[] { 3, 4, 7, 9 },
				pointer);
	}

	private BTreePointer getPointerForKey(final int key) {
		final BTreeRange range = search(key);
		final Iterator4 pointers = range.pointers();
		Assert.isTrue(pointers.moveNext());
		final BTreePointer pointer = (BTreePointer) pointers.current();
		return pointer;
	}

	private void assertReadModePointerIteration(final int[] expectedKeys, BTreePointer pointer) {
		Object[] expected = IntArrays4.toObjectArray(expectedKeys);
		for (int i = 0; i < expected.length; i++) {
			Assert.isNotNull(pointer, "Expected '" + expected[i] + "'");
			Assert.areNotSame(_btree.root(), pointer.node());
			assertInReadMode(pointer.node());
			Assert.areEqual(expected[i], pointer.key());
			assertInReadMode(pointer.node());
			pointer = pointer.next();
		}
	}

	private void assertInReadMode(BTreeNode node) {
		Assert.isFalse(node.canWrite());
	}
	
	protected BTree newBTree() {
		return newBTreeWithNoNodeCaching();
	}

	private BTree newBTreeWithNoNodeCaching() {
		return BTreeAssert.createIntKeyBTree(stream(), 0, 0, BTREE_NODE_SIZE);
	}

}
