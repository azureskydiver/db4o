package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.db4ounit.btree.BTreeAssert;
import com.db4o.db4ounit.foundation.Arrays4;
import com.db4o.inside.btree.BTree;
import com.db4o.inside.fieldindex.*;
import com.db4o.query.*;

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
    
    public void testSingleIndexNotSmaller(){
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(5)).smaller().not();     
        assertExpectedFoos(FieldIndexItem.class, new int[]{7, 9}, query);
    }
    
    public void testSingleIndexNotGreater(){
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(4)).greater().not();     
        assertExpectedFoos(FieldIndexItem.class, new int[]{3, 4}, query);
    }
    
    public void testSingleIndexSmallerOrEqual() {
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(7)).smaller().equal();
        assertExpectedFoos(FieldIndexItem.class, new int[] { 3,4,7 }, query);
    }

    public void testSingleIndexGreaterOrEqual() {
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(7)).greater().equal();
        assertExpectedFoos(FieldIndexItem.class, new int[] { 7, 9 }, query);
    }
    
    public void testSingleIndexRange(){
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(3)).greater();
        query.descend("foo").constrain(new Integer(9)).smaller();
        assertExpectedFoos(FieldIndexItem.class, new int[] { 4, 7 }, query);
    }
    
    public void testSingleIndexAndRange(){
        final Query query = createItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(3)).greater();
        Constraint c2 = query.descend("foo").constrain(new Integer(9)).smaller();
        c1.and(c2);
        assertExpectedFoos(FieldIndexItem.class, new int[] { 4, 7 }, query);
    }
    
    public void _testSingleIndexOrRange(){
        final Query query = createItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(4)).smaller();
        Constraint c2 = query.descend("foo").constrain(new Integer(7)).greater();
        c1.or(c2);
        assertExpectedFoos(FieldIndexItem.class, new int[] { 3, 9 }, query);
    }    
    
    public void _testOrOnDifferentFields(){
        final Query query = createComplexItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(3));
        Constraint c2 = query.descend("bar").constrain(new Integer(8));
        c1.or(c2);
        assertExpectedFoos(ComplexFieldIndexItem.class, new int[] { 3, 7, 9 }, query);
    }
    
    public void _testCantOptimizeDifferentLevels(){
        final Query query = createComplexItemQuery();
        Constraint c1 = query.descend("child").descend("foo").constrain(new Integer(4)).smaller();
        Constraint c2 = query.descend("foo").constrain(new Integer(7)).greater();
        c1.or(c2);
        assertCantOptimize(query);
    }
	
	private void assertCantOptimize(Query query) {
		final FieldIndexProcessorResult result = executeProcessor(query);
		Assert.isNull(result.found);
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

	public void testMultiTransactionSmallerWithCommit() {
		final Transaction transaction = newTransaction();
		fillTransactionWith(transaction, 0);
		
		int[] expectedZeros = newBTreeNodeSizedArray(0);
		assertSmaller(transaction, expectedZeros, 3);
		
		transaction.commit();
		
		fillTransactionWith(transaction, 5);
        assertSmaller(Arrays4.concat(expectedZeros, new int[] { 3, 4 }), 7);
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

    public void testMultiTransactionGreater() {
        fillTransactionWith(systemTrans(), 10);
        fillTransactionWith(systemTrans(), 5);      
        assertGreater(new int[] { 4, 7, 9 }, 3);
        removeFromTransaction(systemTrans(), 5);
        assertGreater(new int[] { 4, 7, 9 }, 3);
        removeFromTransaction(systemTrans(), 10);
        assertGreater(new int[] { 4, 7, 9 }, 3);
    }
	
    public void testSingleIndexEquals() {
        final int expectedBar = 3;
        assertExpectedFoos(FieldIndexItem.class, new int[] { expectedBar }, createQuery(expectedBar));
    }
    
	public void testSingleIndexSmaller() {
		assertSmaller(new int[] { 3, 4 }, 7);
	}

	public void testSingleIndexGreater() {
		assertGreater(new int[] { 4, 7, 9 }, 3);
	}
	
	private void assertGreater(int[] expectedFoos, int greaterThan) {
		final Query query = createItemQuery();
		query.descend("foo").constrain(new Integer(greaterThan)).greater();		
		assertExpectedFoos(FieldIndexItem.class, expectedFoos, query);
	}
	
	private void assertExpectedFoos(Class itemClass, final int[] expectedFoos, final Query query) {
		final Transaction trans = transactionFromQuery(query);
		final int[] expectedIds = mapToObjectIds(createQuery(trans, itemClass), expectedFoos);
		assertExpectedIDs(expectedIds, query);
	}
	
	private void assertExpectedIDs(final int[] expectedIds, final Query query) {
		final FieldIndexProcessorResult result = executeProcessor(query);		
		if (expectedIds.length == 0) {
			Assert.areSame(FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH, result);
			return;
		}
		Assert.isNotNull(result.found);
				 
		assertTreeInt(expectedIds, result.found);
	}

	private FieldIndexProcessorResult executeProcessor(final Query query) {
		return createProcessor(query).run();
	}

	private Transaction transactionFromQuery(Query query) {
		return ((QQuery)query).getTransaction();
	}

	private BTree btree(){
        return fieldIndexBTree(FieldIndexItem.class, "foo");
    }

	private void store(final Transaction trans, final FieldIndexItem item) {
		stream().set(trans, item);
	}
	
	private void fillTransactionWith(Transaction trans, final int bar) {
		for (int i=0; i<BTreeAssert.fillSize(btree()); ++i) {
			store(trans, new FieldIndexItem(bar));
		}
	}

	private int[] newBTreeNodeSizedArray(int value) {
		final BTree btree = btree();
		return BTreeAssert.newBTreeNodeSizedArray(btree, value);
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
