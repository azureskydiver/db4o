package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.QQueryBase.CreateCandidateCollectionResult;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.FieldIndexProcessor;

import db4ounit.Assert;


public class FieldIndexProcessorTestCase extends FieldIndexTestCaseBase {
	
	public static void main(String[] args) {
		new FieldIndexProcessorTestCase().runSolo();
	}
	
	public void testProcessor() {
		final int expectedID = IDS[0];
		final QQuery query = (QQuery) createQuery(expectedID);
		final QCandidates candidates = getQCandidates(query);
		
		final FieldIndexProcessor processor = new FieldIndexProcessor(candidates);
		final Tree tree = processor.run();
		Assert.isNotNull(tree);
		
		final ExpectingVisitor visitor = createExpectingVisitor(expectedID); 
		tree.traverse(new Visitor4() {
			public void visit(Object obj) {
				visitor.visit(new Integer(((TreeInt)obj)._key));
			}
		});
		visitor.assertExpectations();
	}
 
	private QCandidates getQCandidates(final QQuery query) {
		final CreateCandidateCollectionResult result = query.createCandidateCollection();
		QCandidates candidates = (QCandidates)result.candidateCollection._element;
		return candidates;
	}

}
