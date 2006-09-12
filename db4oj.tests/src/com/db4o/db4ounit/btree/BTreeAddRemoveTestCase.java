package com.db4o.db4ounit.btree;



public class BTreeAddRemoveTestCase extends BTreeTestCaseBase {	

	public void testSingleRemoveAdd() {
		
		final Integer element = new Integer(1);
		add(element);		
		assertSize(1);
		
		remove(element);		
		assertSize(0);
		
		add(element);
		
		assertSingleElement(element);
	}

	public void testSingleRemoveAddNull() {
		
		final Integer element = null;
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
	
	public static void main(String[] args) {
		new BTreeAddRemoveTestCase().runSolo();
	}
}
