package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.QQueryBase.CreateCandidateCollectionResult;
import com.db4o.db4ounit.btree.*;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.FieldIndexProcessor;
import com.db4o.inside.btree.BTree;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;

import db4ounit.Assert;


public class FieldIndexProcessorTestCase extends FieldIndexTestCaseBase {
	
	public static void main(String[] args) {
		new FieldIndexProcessorTestCase().runSolo();
	}
	
	public void store() {
		store(new int[] { 3, 4, 7, 9 });
	}
	
	public void testSingleIndexEquals() {
		final int expectedBar = 3;
		assertQueryMatch(new int[] { expectedBar }, createQuery(expectedBar));
	}
	
	public void testMultiTransactionSmaller() {
		fillSystemTransactionWith(0);
		fillSystemTransactionWith(5);
        assertSmaller(new int[] { 3, 4 }, 7);
	}

	public void testSingleIndexSmaller() {
		assertSmaller(new int[] { 3, 4 }, 7);
	}

	public void testSingleIndexGreater() {
		assertGreater(new int[] { 4, 7, 9 }, 3);
	}
	
	public void testMultiTransactionGreater() {
		fillSystemTransactionWith(10);
		fillSystemTransactionWith(5);		
		assertGreater(new int[] { 4, 7, 9 }, 3);
		removeFromSystemTransaction(5);
        assertGreater(new int[] { 4, 7, 9 }, 3);
		removeFromSystemTransaction(10);
		assertGreater(new int[] { 4, 7, 9 }, 3);
	}

	private void assertGreater(int[] expectedBars, int greaterThan) {
		final Query query = createItemQuery();
		query.descend("bar").constrain(new Integer(greaterThan)).greater();		
		assertQueryMatch(expectedBars, query);
	}
	
	public void testSingleIndexGreaterOrEqual() {
		final Query query = createItemQuery();
		query.descend("bar").constrain(new Integer(7)).greater().equal();
		
		assertQueryMatch(new int[] { 7, 9 }, query);
	}

	private void assertQueryMatch(final int[] expectedBars, final Query query) {
		final QCandidates candidates = getQCandidates(query);
		
		final FieldIndexProcessor processor = new FieldIndexProcessor(candidates);
		final Tree tree = processor.run();
		Assert.isNotNull(tree);
		
		final ExpectingVisitor visitor = createExpectingVisitor(mapToObjectIds(expectedBars)); 
		tree.traverse(new Visitor4() {
			public void visit(Object obj) {
				visitor.visit(new Integer(((TreeInt)obj)._key));
			}
		});
		visitor.assertExpectations();
	}
 
	private int[] mapToObjectIds(int[] bars) {
		int[] objectIds = new int[bars.length];
		for (int i=0; i<objectIds.length; ++i) {
			objectIds[i] = idFromBar(bars[i]);
		}
		return objectIds;
	}

	private int idFromBar(int bar) {
		final ObjectSet set = db().get(FieldIndexItem.class);
		while (set.hasNext()) {
			FieldIndexItem item = (FieldIndexItem)set.next();
			if (item.bar == bar) {
				return (int) db().getID(item);
			}
		}
		throw new IllegalArgumentException();
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
        YapStream stream = (YapStream)db();
        final ReflectClass reflectClass = stream.reflector().forClass(FieldIndexItem.class);
        return stream.getYapClass(reflectClass, false).getYapField("bar").getIndex();
    }

	private void store(final Transaction trans, final FieldIndexItem item) {
		((YapStream)db()).set(trans, item);
	}
	
	private void fillSystemTransactionWith(final int bar) {
		for (int i=0; i<btreeNodeSize()+1; ++i) {
			store(systemTrans(), new FieldIndexItem(bar));
		}
	}
	
	private void removeFromSystemTransaction(final int bar) {
		final ObjectSet found = createItemQuery(systemTrans()).execute();
		while (found.hasNext()) {
			FieldIndexItem item = (FieldIndexItem)found.next();
			if (item.bar == bar) {
				stream().delete(systemTrans(), item);
			}
		}
	}
	
	private void assertSmaller(final int[] expectedBars, final int smallerThan) {
		final Query query = createItemQuery();
		query.descend("bar").constrain(new Integer(smallerThan)).smaller();
		assertQueryMatch(expectedBars, query);
	}
}
