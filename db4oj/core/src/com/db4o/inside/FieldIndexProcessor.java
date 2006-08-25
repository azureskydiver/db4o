package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;

public class FieldIndexProcessor {

	private QCandidates _candidates;

	public FieldIndexProcessor(QCandidates candidates) {
		_candidates = candidates;
	}
	
	public FieldIndexProcessorResult run() {
		IndexedNode bestIndex = selectBestIndex();
		if (null == bestIndex) {
			return FieldIndexProcessorResult.NO_INDEX_FOUND;
		}
		if (bestIndex.resultSize() > 0) {
			return new FieldIndexProcessorResult(bestIndex.toTreeInt());
		}
		return FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH;
	}
	
	public IndexedNode selectBestIndex() {		
		final Iterator4 i = selectIndexes();
		if (!i.hasNext()) {
			return null;
		}
		
		IndexedNode best = (IndexedNode)i.next();
		while (i.hasNext()) {
			IndexedNode leaf = (IndexedNode)i.next();
			if (leaf.resultSize() < best.resultSize()) {
				best = leaf;
			}
		}
		return best;
	}

	private Transaction transaction() {
		return _candidates.i_trans;
	}

	private Iterator4 selectIndexes() {
		final Collection4 leaves = new Collection4();
		collectIndexedLeaves(leaves, _candidates.iterateConstraints());		
		return leaves.iterator();
	}

	private void collectIndexedLeaves(final Collection4 leaves, final Iterator4 qcons) {
		while (qcons.hasNext()) {
			QCon qcon = (QCon)qcons.next();
			if (isLeaf(qcon)) {
				if (qcon.canLoadByIndex() && qcon instanceof QConObject) {
					leaves.add(new IndexedNode(transaction(), (QConObject) qcon));
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
