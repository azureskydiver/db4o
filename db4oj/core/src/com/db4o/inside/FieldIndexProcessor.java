package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;

public class FieldIndexProcessor {

	private QCandidates _candidates;

	public FieldIndexProcessor(QCandidates candidates) {
		_candidates = candidates;
	}
	
	public FieldIndexProcessorResult run() {
		final Iterator4 i = findIndexedLeaves();
		if (!i.hasNext()) {
			return FieldIndexProcessorResult.NO_INDEX_FOUND;
		}
		while (i.hasNext()) {
			IndexedLeaf leaf = (IndexedLeaf)i.next();
			if (leaf.resultSize() > 0) {
				return new FieldIndexProcessorResult(leaf.toTreeInt());
			}
		}
		return FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH;
	}

	private Transaction transaction() {
		return _candidates.i_trans;
	}

	private Iterator4 findIndexedLeaves() {
		final Collection4 leaves = new Collection4();
		collectIndexedLeaves(leaves, _candidates.iterateConstraints());
		return leaves.iterator();
	}

	private void collectIndexedLeaves(final Collection4 leaves, final Iterator4 qcons) {
		while (qcons.hasNext()) {
			QCon qcon = (QCon)qcons.next();
			if (isLeaf(qcon)) {
				if (qcon.canLoadByIndex() && qcon instanceof QConObject) {
					leaves.add(new IndexedLeaf(transaction(), (QConObject) qcon));
				}
			} else {
				collectIndexedLeaves(leaves, qcon.iterateChildren());
			}
		}
	}

	private boolean isLeaf(QCon qcon) {
		return !qcon.hasChildren();
	}
    
    
    
    
}
