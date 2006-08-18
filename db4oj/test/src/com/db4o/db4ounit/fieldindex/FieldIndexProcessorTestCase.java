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
	
	public void testProcessor() {
		final int expectedID = IDS[0];
		final QCandidates candidates = getQCandidates(createQuery(expectedID));
		
		final FieldIndexProcessor processor = new FieldIndexProcessor(candidates);
		final Tree tree = processor.run();
		Assert.isNotNull(tree);
		
		final ExpectingVisitor visitor = createExpectingVisitor(objectContainerIdFromIdFieldValue(expectedID)); 
		tree.traverse(new Visitor4() {
			public void visit(Object obj) {
				visitor.visit(new Integer(((TreeInt)obj)._key));
			}
		});
		visitor.assertExpectations();
	}
 
	private int objectContainerIdFromIdFieldValue(int id) {
		final ObjectSet set = db().get(FieldIndexItem.class);
		while (set.hasNext()) {
			FieldIndexItem item = (FieldIndexItem)set.next();
			if (item.id == id) {
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
