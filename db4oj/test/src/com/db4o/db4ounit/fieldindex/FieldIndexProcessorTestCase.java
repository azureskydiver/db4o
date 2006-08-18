package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.QQueryBase.CreateCandidateCollectionResult;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.FieldIndexProcessor;
import com.db4o.query.Query;

import db4ounit.Assert;


public class FieldIndexProcessorTestCase extends FieldIndexTestCaseBase {
	
	public static void main(String[] args) {
		new FieldIndexProcessorTestCase().runSolo();
	}
	
	public void testSingleIndexEquals() {
		final int expectedBar = 3;
		assertQueryMatch(new int[] { expectedBar }, createQuery(expectedBar));
	}
	
	public void testSingleIndexGreater() {
		final Query query = createItemQuery();
		query.descend("bar").constrain(new Integer(3)).greater();
		
		assertQueryMatch(new int[] { 4, 7, 9 }, query);
	}
	
//	public void testSingleIndexGreaterOrEqual() {
//		final Query query = createItemQuery();
//		query.descend("bar").constrain(new Integer(7)).greater().equal();
//		
//		assertQueryMatch(new int[] { 7, 9 }, query);
//	}

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

}
