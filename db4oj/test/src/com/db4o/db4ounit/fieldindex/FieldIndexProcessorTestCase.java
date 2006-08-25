package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.QQueryBase.CreateCandidateCollectionResult;
import com.db4o.db4ounit.btree.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.btree.BTree;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

import db4ounit.Assert;


public class FieldIndexProcessorTestCase extends FieldIndexTestCaseBase {
	
	public static void main(String[] args) {
		new FieldIndexProcessorTestCase().runSolo();
	}
	
	protected void configure() {
		super.configure();
		index(ComplexFieldIndexItem.class, "foo");
		index(ComplexFieldIndexItem.class, "bar");
		index(ComplexFieldIndexItem.class, "child");
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

	private Query createComplexItemQuery() {
		return createQuery(ComplexFieldIndexItem.class);
	}

	private void assertBestIndex(String expectedFieldIndex, final Query query) {
		IndexedNode node = selectBestIndex(query);
		assertComplexItemIndex(expectedFieldIndex, node);
	}

	private void assertComplexItemIndex(String expectedFieldIndex, IndexedNode leaf) {
		Assert.areSame(complexItemIndex(expectedFieldIndex), leaf.getIndex());
	}

	private IndexedNode selectBestIndex(final Query query) {
		final FieldIndexProcessor processor = createProcessor(query);		
		return processor.selectBestIndex();
	}
	
	public void testIndexDescending() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("foo").constrain(new Integer(3));
		query.descend("bar").constrain(new Integer(2));
		
		final IndexedNode index = selectBestIndex(query);
		assertComplexItemIndex("foo", index);
		
//		Assert.isFalse(index.isResolved());
//		
//		IndexedNode result = index.resolve();
//		assertComplexItemIndex("child", result);
//		
//		Assert.isTrue(result.isResolved());
//		
//		assertTreeInt(
//				mapToObjectIds(createComplexItemQuery(), new int[] { 4 }),
//				result.toTreeInt());
	}

	public void testSingleIndexEquals() {
		final int expectedBar = 3;
		assertExpectedFoos(new int[] { expectedBar }, createQuery(expectedBar));
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
		assertExpectedFoos(expectedBars, query);
	}
	
	public void testSingleIndexGreaterOrEqual() {
		final Query query = createItemQuery();
		query.descend("foo").constrain(new Integer(7)).greater().equal();
		
		assertExpectedFoos(new int[] { 7, 9 }, query);
	}

	private void assertExpectedFoos(final int[] expectedFoos, final Query query) {
		
		final Transaction trans = transactionFromQuery(query);
		final int[] expectedIds = mapToObjectIds(createItemQuery(trans), expectedFoos);
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

	private void assertTreeInt(final int[] expectedValues, final TreeInt treeInt) {
		final ExpectingVisitor visitor = createExpectingVisitor(expectedValues);
		treeInt.traverse(new Visitor4() {
			public void visit(Object obj) {
				visitor.visit(new Integer(((TreeInt)obj)._key));
			}
		});
		visitor.assertExpectations();
	}

	private FieldIndexProcessor createProcessor(final Query query) {
		final QCandidates candidates = getQCandidates(query);		
		final FieldIndexProcessor processor = new FieldIndexProcessor(candidates);
		return processor;
	}
 
	private Transaction transactionFromQuery(Query query) {
		return ((QQuery)query).getTransaction();
	}

	private int[] mapToObjectIds(Query itemQuery, int[] foos) {
		int[] lookingFor = clone(foos);
		
		int[] objectIds = new int[foos.length];
		final ObjectSet set = itemQuery.execute();
		while (set.hasNext()) {
			HasFoo item = (HasFoo)set.next();
			for (int i = 0; i < lookingFor.length; i++) {
				if(lookingFor[i] == item.getFoo()){
					lookingFor[i] = -1;
					objectIds[i] = (int) db().getID(item);
					break;
				}
			}
		}		
		
		if (!all(lookingFor, -1)) {
			throw new IllegalArgumentException();
		}
		
		return objectIds;
	}

	private boolean all(int[] array, int value) {
		for (int i=0; i<array.length; ++i) {
			if (value != array[i]) {
				return false;
			}
		}
		return true;
	}

	private int[] clone(int[] bars) {
		int[] array = new int[bars.length];
		System.arraycopy(bars, 0, array, 0, bars.length);
		return array;
	}

	private QCandidates getQCandidates(final Query query) {
		final CreateCandidateCollectionResult result = ((QQuery)query).createCandidateCollection();
		QCandidates candidates = (QCandidates)result.candidateCollection._element;
		return candidates;
	}
	
	private int btreeNodeSize() {		
		return btree().nodeSize();
	}
    
    private BTree btree(){
        return fieldIndexBTree(FieldIndexItem.class, "foo");
    }

	private BTree fieldIndexBTree(Class clazz, String fieldName) {
		final ReflectClass reflectClass = stream().reflector().forClass(clazz);
        return stream().getYapClass(reflectClass, false).getYapField(fieldName).getIndex();
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
		assertExpectedFoos(expectedFoos, query);
	}
	
	private Transaction newTransaction() {
		return stream().newTransaction();
	}
	
	private void storeComplexItems(int[] foos, int[] bars) {
		ComplexFieldIndexItem last = null;
		for (int i = 0; i < foos.length; i++) {
			last = new ComplexFieldIndexItem(foos[i], bars[i], last);
			store(last);
	    }
	}
	
	private BTree complexItemIndex(String fieldName) {
		return fieldIndexBTree(ComplexFieldIndexItem.class, fieldName);
	}

}
