/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.btree;


import com.db4o.Transaction;
import com.db4o.inside.btree.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public abstract class BTreeTestCaseBase extends AbstractDb4oTestCase{

	protected BTree _btree;
	
	protected void db4oSetupAfterStore() throws Exception {
		_btree = newBTree();
	}

	protected BTree newBTree() {
		return BTreeAssert.createIntKeyBTree(stream(), 0);
	}
	
	protected BTreeRange range(int lower, int upper) {
		final BTreeRange lowerRange = search(lower);
		final BTreeRange upperRange = search(upper);
		return lowerRange.extendToLastOf(upperRange);
	}

	protected BTreeRange search(int key) {
		return search(trans(), key);
	}

	protected void add(int[] keys) {
		for (int i=0; i<keys.length; ++i) {
			add(keys[i]);
		}
	}

	protected BTreeRange search(Transaction trans, int key) {
		return _btree.search(trans, new Integer(key));
	}

	protected void commit(Transaction trans) {
		_btree.commit(trans);
	}
	
	protected void commit() {
		commit(trans());
	}

	protected void remove(Transaction transaction, int[] keys) {
		for (int i = 0; i < keys.length; i++) {
			remove(transaction, keys[i]);
		}
	}

	protected void add(Transaction transaction, int[] keys) {
		for (int i = 0; i < keys.length; i++) {
			add(transaction, keys[i]);
		}
	}

	protected void assertEmpty(Transaction transaction) {
		BTreeAssert.assertEmpty(transaction, _btree);
	}

	protected void add(Transaction transaction, int element) {
		_btree.add(transaction, new Integer(element));
	}

	protected void remove(final int element) {
		remove(trans(), element);
	}
	
	protected void remove(final Transaction trans, final int element) {
		_btree.remove(trans, new Integer(element));
	}

	protected void add(int element) {
		add(trans(), element);
	}

	private int size() {
		return _btree.size(trans());
	}

	protected void assertSize(int expected) {
		Assert.areEqual(expected, size());
	}

	protected void assertSingleElement(final int element) {
		assertSingleElement(trans(), element);
	}
	
	protected void assertSingleElement(final Transaction trans, final int element) {
		BTreeAssert.assertSingleElement(trans, _btree, new Integer(element));
	}
}
