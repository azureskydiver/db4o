package com.db4o.db4ounit.common.btree;

import com.db4o.*;
import com.db4o.db4ounit.common.foundation.IntArrays4;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;

import db4ounit.Assert;

public class BTreeAssert {

	public static ExpectingVisitor createExpectingVisitor(int value, int count) {
	    int[] values = new int[count];
	    for (int i = 0; i < values.length; i++) {
	        values[i] = value;
	    }
	    return new ExpectingVisitor(IntArrays4.toObjectArray(values));
	}

	public static ExpectingVisitor createExpectingVisitor(int[] keys) {
		return new ExpectingVisitor(IntArrays4.toObjectArray(keys));
	}
	
	private static ExpectingVisitor createSortedExpectingVisitor(int[] keys) {
		return new ExpectingVisitor(IntArrays4.toObjectArray(keys), true, false);
	}

	public static void traverseKeys(BTreeRange result, Visitor4 visitor) {
		final Iterator4 i = result.keys();
		while (i.moveNext()) {
			visitor.visit(i.current());
		}
	}

	public static void assertKeys(final Transaction transaction, BTree btree, final int[] keys) {
		final ExpectingVisitor visitor = createExpectingVisitor(keys);
		btree.traverseKeys(transaction, visitor);
		visitor.assertExpectations();
	}

	public static void assertEmpty(Transaction transaction, BTree tree) {
	    final ExpectingVisitor visitor = new ExpectingVisitor(new Object[0]);
	    tree.traverseKeys(transaction, visitor);
		visitor.assertExpectations();
	    Assert.areEqual(0, tree.size(transaction));
	}

	public static void dumpKeys(Transaction trans, BTree tree) {
		tree.traverseKeys(trans, new Visitor4() {
			public void visit(Object obj) {
				System.out.println(obj);
			}
		});
	}

	public static ExpectingVisitor createExpectingVisitor(final int expectedID) {
		return createExpectingVisitor(expectedID, 1);
	}

	public static int fillSize(BTree btree) {
		return btree.nodeSize()+1;
	}

	public static int[] newBTreeNodeSizedArray(final BTree btree, int value) {
		return IntArrays4.fill(new int[fillSize(btree)], value);
	}

	public static void assertRange(int[] expectedKeys, BTreeRange range) {
		Assert.isNotNull(range);
		final ExpectingVisitor visitor = createSortedExpectingVisitor(expectedKeys);
		
		traverseKeys(range, visitor);
		visitor.assertExpectations();
	}

	public static BTree createIntKeyBTree(final YapStream stream, int id) {
		return new BTree(stream.getSystemTransaction(), id, new YInt(stream), null, 4, stream.configImpl().bTreeCacheHeight());
	}
	
	public static BTree createIntKeyBTree(final YapStream stream, int id, int treeCacheHeight) {
		return new BTree(stream.getSystemTransaction(), id, new YInt(stream), null, 4, treeCacheHeight);
	}

	public static void assertSingleElement(Transaction trans, BTree btree, Object element) {
		Assert.areEqual(1, btree.size(trans));
		
		final BTreeRange result = btree.search(trans, element);
		ExpectingVisitor expectingVisitor = new ExpectingVisitor(new Object[] { element });
		BTreeAssert.traverseKeys(result, expectingVisitor);
		expectingVisitor.assertExpectations();
		
		expectingVisitor = new ExpectingVisitor(new Object[] { element });
		btree.traverseKeys(trans, expectingVisitor);
		expectingVisitor.assertExpectations();
	}

}
