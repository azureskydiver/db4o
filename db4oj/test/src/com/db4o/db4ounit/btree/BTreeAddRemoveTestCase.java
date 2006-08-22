package com.db4o.db4ounit.btree;

import com.db4o.Transaction;
import com.db4o.inside.btree.BTree;
import com.db4o.inside.btree.BTreeRange;

import db4ounit.Assert;

public class BTreeAddRemoveTestCase extends BTreeTestCaseBase {
	
	private BTree _tree;
	
	public void setUp() throws Exception {
		super.setUp();
		_tree = createIntKeyBTree(0);
	}

	public void testSingleRemoveAdd() {
		
		final Integer element = new Integer(1);
		add(element);		
		assertSize(1);
		
		remove(element);		
		assertSize(0);
		
		add(element);
		
		assertSingleElement(element);
	}
	
	public void testMultipleRemoveAdds() {
		
		final Integer element = new Integer(1);
		
		add(element);
		remove(element);
		remove(element);
		add(element);
		
		assertSingleElement(element);
	}
	
	public void testAddRemoveInDifferentTransactions() {
		
		final Integer element = new Integer(1);
		
		add(trans(), element);
		add(systemTrans(), element);
		
		remove(systemTrans(), element);
		remove(trans(), element);
		
		assertEmpty(systemTrans());
		assertEmpty(trans());
	} 
	
	public void testRemoveAddInDifferentTransactions() {
		final Integer element = new Integer(1);
		
		add(element);
		
		db().commit();
		
		remove(trans(), element);
		remove(systemTrans(), element);
		
		assertEmpty(systemTrans());
		assertEmpty(trans());
		
		add(trans(), element);
		assertSingleElement(trans(), element);
		
		add(systemTrans(), element);
		assertSingleElement(systemTrans(), element);
	}
	
	private void assertEmpty(Transaction transaction) {
		assertEmpty(transaction, _tree);
	}

    private void add(Transaction transaction, Integer element) {
		_tree.add(transaction, element);
	}

	private void remove(final Integer element) {
		remove(trans(), element);
	}

	private void remove(final Transaction trans, final Integer element) {
		_tree.remove(trans, element);
	}

	private void add(final Integer element) {
		_tree.add(trans(), element);
	}

	private int size() {
		return _tree.size(trans());
	}
	
	private void assertSize(int expected) {
		Assert.areEqual(expected, size());
	}
	
	private void assertSingleElement(final Integer element) {
		assertSingleElement(trans(), element);
	}

	private void assertSingleElement(final Transaction trans, final Integer element) {
		Assert.areEqual(1, _tree.size(trans));
		
		final BTreeRange result = _tree.search(trans, element);
		ExpectingVisitor expectingVisitor = new ExpectingVisitor(new Object[] { element });
		traverseKeys(result, expectingVisitor);
		expectingVisitor.assertExpectations();
		
		expectingVisitor = new ExpectingVisitor(new Object[] { element });
		_tree.traverseKeys(trans, expectingVisitor);
		expectingVisitor.assertExpectations();
	}
	
	
	public static void main(String[] args) {
		new BTreeAddRemoveTestCase().runSolo();
	}
}
