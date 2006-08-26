package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.btree.BTree;
import com.db4o.query.Query;

import db4ounit.Assert;


public class FieldIndexProcessorTestCase extends FieldIndexProcessorTestCaseBase {
	
	public static void main(String[] args) {
		new FieldIndexProcessorTestCase().runSolo();
	}
	
	public void store() {
		storeItems(new int[] { 3, 4, 7, 9 });
		storeComplexItems(
						new int[] { 3, 4, 7, 9 },
						new int[] { 2, 2, 8, 8 });
	}
	
	public void testIndexSelection() {		
		Query query = createComplexItemQuery();		
		query.descend("bar").constrain(new Integer(2));
		query.descend("foo").constrain(new Integer(3));
		
		assertBestIndex("foo", query);
		
		query = createComplexItemQuery();
		query.descend("foo").constrain(new Integer(3));
		query.descend("bar").constrain(new Integer(2));
		
		assertBestIndex("foo", query);
	}

	private void assertBestIndex(String expectedFieldIndex, final Query query) {
		IndexedNode node = selectBestIndex(query);
		assertComplexItemIndex(expectedFieldIndex, node);
	}

	public void testDoubleDescendingOnQuery() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("foo").constrain(new Integer(3));
		
		assertExpectedFoos(ComplexFieldIndexItem.class, new int[] { 4 }, query);
	}
	
	public void testTripleDescendingOnQuery() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("child").descend("foo").constrain(new Integer(3));
		
		assertExpectedFoos(ComplexFieldIndexItem.class, new int[] { 7 }, query);
	}

	public void testSingleIndexEquals() {
		final int expectedBar = 3;
		assertExpectedFoos(FieldIndexItem.class, new int[] { expectedBar }, createQuery(expectedBar));
	}
	
	public void testMultiTransactionSmallerWithCommit() {
		final Transaction transaction = newTransaction();
		fillTransactionWith(transaction, 0);
		
		int[] expectedZeros = newBTreeNodeSizedArray(0);
		assertSmaller(transaction, expectedZeros, 3);
		
		transaction.commit();
		
		fillTransactionWith(transaction, 5);
        assertSmaller(concat(expectedZeros, new int[] { 3, 4 }), 7);
	}

	public void testMultiTransactionWithRollback() {
		final Transaction transaction = newTransaction();
		fillTransactionWith(transaction, 0);
		
		int[] expectedZeros = newBTreeNodeSizedArray(0);
		assertSmaller(transaction, expectedZeros, 3);
		
		transaction.rollback();
		
		assertSmaller(transaction, new int[0], 3);
		
		fillTransactionWith(transaction, 5);
        assertSmaller(new int[] { 3, 4 }, 7);
	}
	
	public void testMultiTransactionSmaller() {
		final Transaction transaction = newTransaction();
		fillTransactionWith(transaction, 0);
		
		int[] expected = newBTreeNodeSizedArray(0);
		assertSmaller(transaction, expected, 3);
		
		fillTransactionWith(transaction, 5);
        assertSmaller(new int[] { 3, 4 }, 7);
	}

	public void testSingleIndexSmaller() {
		assertSmaller(new int[] { 3, 4 }, 7);
	}

	public void testSingleIndexGreater() {
		assertGreater(new int[] { 4, 7, 9 }, 3);
	}
	
	public void testMultiTransactionGreater() {
		fillTransactionWith(systemTrans(), 10);
		fillTransactionWith(systemTrans(), 5);		
		assertGreater(new int[] { 4, 7, 9 }, 3);
		removeFromTransaction(systemTrans(), 5);
        assertGreater(new int[] { 4, 7, 9 }, 3);
		removeFromTransaction(systemTrans(), 10);
		assertGreater(new int[] { 4, 7, 9 }, 3);
	}

	private void assertGreater(int[] expectedBars, int greaterThan) {
		final Query query = createItemQuery();
		query.descend("foo").constrain(new Integer(greaterThan)).greater();		
		assertExpectedFoos(FieldIndexItem.class, expectedBars, query);
	}
	
	public void testSingleIndexGreaterOrEqual() {
		final Query query = createItemQuery();
		query.descend("foo").constrain(new Integer(7)).greater().equal();
		
		assertExpectedFoos(FieldIndexItem.class, new int[] { 7, 9 }, query);
	}

	private void assertExpectedFoos(Class itemClass, final int[] expectedFoos, final Query query) {
		
		final Transaction trans = transactionFromQuery(query);
		final int[] expectedIds = mapToObjectIds(createQuery(trans, itemClass), expectedFoos);
		assertExpectedIDs(expectedIds, query);
	}
	
	private void assertExpectedIDs(final int[] expectedIds, final Query query) {
		final FieldIndexProcessor processor = createProcessor(query);
		final FieldIndexProcessorResult result = processor.run();		
		if (expectedIds.length == 0) {
			Assert.areSame(FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH, result);
			return;
		}
		Assert.isNotNull(result.found);
				 
		assertTreeInt(expectedIds, result.found);
	}

	private Transaction transactionFromQuery(Query query) {
		return ((QQuery)query).getTransaction();
	}

	private int btreeNodeSize() {		
		return btree().nodeSize();
	}
    
    private BTree btree(){
        return fieldIndexBTree(FieldIndexItem.class, "foo");
    }

	private void store(final Transaction trans, final FieldIndexItem item) {
		stream().set(trans, item);
	}
	
	private void fillTransactionWith(Transaction trans, final int bar) {
		for (int i=0; i<fillSize(); ++i) {
			store(trans, new FieldIndexItem(bar));
		}
	}

	private int fillSize() {
		return btreeNodeSize()+1;
	}
	
	private int[] newBTreeNodeSizedArray(int value) {
		return fill(new int[fillSize()], value);
	}

	private int[] fill(int[] array, int value) {
		for (int i=0; i<array.length; ++i) {
			array[i] = value;
		}
		return array;
	}
	
	private int[] concat(int[] a, int[] b) {
		int[] array = new int[a.length + b.length];
		System.arraycopy(a, 0, array, 0, a.length);
		System.arraycopy(b, 0, array, a.length, b.length);
		return array;
	}
	
	private void removeFromTransaction(Transaction trans, final int foo) {
		final ObjectSet found = createItemQuery(trans).execute();
		while (found.hasNext()) {
			FieldIndexItem item = (FieldIndexItem)found.next();
			if (item.foo == foo) {
				stream().delete(trans, item);
			}
		}
	}
	
	private void assertSmaller(final int[] expectedFoos, final int smallerThan) {
		assertSmaller(trans(), expectedFoos, smallerThan);
	}

	private void assertSmaller(final Transaction transaction, final int[] expectedFoos, final int smallerThan) {
		final Query query = createItemQuery(transaction);
		query.descend("foo").constrain(new Integer(smallerThan)).smaller();
		assertExpectedFoos(FieldIndexItem.class, expectedFoos, query);
	}
	
	private Transaction newTransaction() {
		return stream().newTransaction();
	}

}
