/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

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
			IndexedNode resolved = resolveFully(bestIndex);
			if (null == resolved) {
				return FieldIndexProcessorResult.NO_INDEX_FOUND;
			}
			return new FieldIndexProcessorResult(resolved);
		}
		return FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH;
	}

	private IndexedNode resolveFully(IndexedNode bestIndex) {
		if (null == bestIndex) {
			return null;
		}
		if (bestIndex.isResolved()) {
			return bestIndex;
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
