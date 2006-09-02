package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;

public class FieldIndexProcessor {

	private final QCandidates _candidates;

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
		final Iterator4 i = collectIndexedNodes();
		if (!i.moveNext()) {
			return null;
		}
		
		IndexedNode best = (IndexedNode)i.current();
		while (i.moveNext()) {
			IndexedNode leaf = (IndexedNode)i.current();
			if (leaf.resultSize() < best.resultSize()) {
				best = leaf;
			}
		}
		return best;
	}

	public Iterator4 collectIndexedNodes() {
		return new IndexedNodeCollector(_candidates).getNodes();
	}	    
}
