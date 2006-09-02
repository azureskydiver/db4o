package com.db4o.inside.fieldindex;

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
			return new FieldIndexProcessorResult(resolveFully(bestIndex));
		}
		return FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH;
	}

	private TreeInt resolveFully(IndexedNode bestIndex) {
		if (bestIndex.isResolved()) {
			return bestIndex.toTreeInt();
		}
		return resolveFully(bestIndex.resolve());
	}
	
	public IndexedNode selectBestIndex() {		
		final Iterator4 i = selectIndexes();
		if (!i.moveNext()) {
			return null;
		}
		
		IndexedNode best = (IndexedNode)i.current();
		while (i.moveNext()) {
			IndexedLeaf leaf = (IndexedLeaf)i.current();
			if (leaf.resultSize() < best.resultSize()) {
				best = leaf;
			}
		}
		return best;
	}

	private Iterator4 selectIndexes() {
		final Collection4 leaves = new Collection4();
		collectIndexedLeaves(leaves, _candidates.iterateConstraints());		
		return leaves.iterator();
	}

	private void collectIndexedLeaves(final Collection4 leaves, final Iterator4 qcons) {
		
		while (qcons.moveNext()) {
			QCon qcon = (QCon)qcons.current();
			if (isLeaf(qcon)) {
				if (qcon.canLoadByIndex() && qcon instanceof QConObject) {
					
					final QConObject conObject = (QConObject) qcon;
					IndexedLeaf leaf = findLeafOnSameField(leaves, conObject);
					if (leaf != null) {
						leaves.remove(leaf);
						leaves.add(join(leaf, conObject));
					} else {
						leaves.add(new IndexedLeaf(conObject));
					}
				}
			} else {
				collectIndexedLeaves(leaves, qcon.iterateChildren());
			}
		}
	}

	private IndexedNode join(IndexedLeaf existing, final QConObject conObject) {
		if (existing.constraint().hasOrJoinWith(conObject)) {
			return new OrIndexedLeaf(existing, new IndexedLeaf(conObject));
		}
		return new AndIndexedLeaf(existing, new IndexedLeaf(conObject));
	}

	private IndexedLeaf findLeafOnSameField(Collection4 leaves, QConObject conObject) {
		final Iterator4 i = leaves.iterator();
		while (i.moveNext()) {
			IndexedLeaf leaf = (IndexedLeaf)i.current();
			if (conObject.onSameFieldAs(leaf.constraint())) {
				return leaf;
			}
		}
		return null;
	}

	private boolean isLeaf(QCon qcon) {
		return !qcon.hasChildren();
	}
    
}
