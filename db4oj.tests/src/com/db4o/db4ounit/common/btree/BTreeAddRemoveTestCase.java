package com.db4o.db4ounit.common.btree;

import com.db4o.Transaction;
import com.db4o.inside.btree.BTreeRange;

import db4ounit.Assert;

public class BTreeAddRemoveTestCase extends BTreeTestCaseBase {
	
	public void testSingleRemoveAdd() {
		
		final int element = 1;
		add(element);		
		assertSize(1);
		
		remove(element);		
		assertSize(0);
		
		add(element);
		
		assertSingleElement(element);
	}
	
	public void testSearchingRemoved() {
		final int[] keys = new int[] { 3, 4, 7, 9 };
		add(keys);
		remove(4);
		final BTreeRange result = search(4);
		Assert.isTrue(result.isEmpty());
		
		final BTreeRange range = result.greater();
		BTreeAssert.assertRange(new int[] { 7, 9 }, range);
	}

	public void testMultipleRemoveAdds() {
		
		final int element = 1;
		
		add(element);
		remove(element);
		remove(element);
		add(element);
		
		assertSingleElement(element);
	}
	
	public void testMultiTransactionCancelledRemoval() {
		final int element = 1;
		add(element);
		commit();
		
		final Transaction trans1 = newTransaction();
		final Transaction trans2 = newTransaction();
		
		remove(trans1, element);
		assertSingleElement(trans2, element);
		add(trans1, element);
		assertSingleElement(trans1, element);
		assertSingleElement(trans2, element);
		
		trans1.commit();
		assertSingleElement(element);
	}
	
	public void testMultiTransactionSearch() {
		
		final int[] keys = new int[] { 3, 4, 7, 9 };
		add(trans(), keys);
		commit(trans());
		
        final int[] assorted = new int[] { 1, 2, 11, 13, 21, 52, 51, 66, 89, 10 };
		add(systemTrans(), assorted);
		assertKeys(keys);
		
        remove(systemTrans(), assorted);
        assertKeys(keys);
        
        BTreeAssert.assertRange(new int[] { 7, 9 }, search(trans(), 4).greater());
	}

	private void assertKeys(final int[] keys) {
		BTreeAssert.assertKeys(trans(), _btree, keys);
	}

	public void testAddRemoveInDifferentTransactions() {
		
		final int element = 1;
		
		add(trans(), element);
		add(systemTrans(), element);
		
		remove(systemTrans(), element);
		remove(trans(), element);
		
		assertEmpty(systemTrans());
		assertEmpty(trans());
	} 
	
	public void testRemoveAddInDifferentTransactions() {
		final int element = 1;
		
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
	
	public static void main(String[] args) {
		new BTreeAddRemoveTestCase().runSolo();
	}
}
