package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;

public class FieldIndexProcessor {

	private QCandidates _candidates;

	public FieldIndexProcessor(QCandidates candidates) {
		_candidates = candidates;
	}

	public TreeInt run() {
		IndexedLeaf leaf = findBestIndex();
		if (null == leaf) {
			return null;
		}
		return leaf.toTreeInt();
	}

	private IndexedLeaf findBestIndex() {
		final Iterator4 i = findIndexedLeaves();
		while (i.hasNext()) {
			IndexedLeaf leaf = (IndexedLeaf)i.next();
			if (leaf.resultSize() > 0) {
				return leaf;
			}
		}
		return null;
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
