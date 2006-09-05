/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.*;

/**
 * @exclude
 */
class BTreeAlgebra {
	
	static BTreeRange union(BTreeRangeSingle single, BTreeRange range) {
		if (null == range) {
			throw new ArgumentNullException();
		}
		if (range instanceof BTreeRangeSingle) {
			return BTreeAlgebra.unionImpl(single, (BTreeRangeSingle)range);
		}
		return BTreeAlgebra.unionImpl(single, (BTreeRangeUnion)range);
	}
	
	static BTreeRange union(BTreeRangeUnion union, BTreeRange range) {
		if (null == range) {
			throw new ArgumentNullException();
		}
		if (range instanceof BTreeRangeUnion) {
			return BTreeAlgebra.unionImpl(union, (BTreeRangeUnion)range);
		}
		return BTreeAlgebra.unionImpl((BTreeRangeSingle)range, union);
	}
	
	private static BTreeRange unionImpl(BTreeRangeUnion union, BTreeRangeUnion union2) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	private static BTreeRange unionImpl(BTreeRangeSingle single, BTreeRangeUnion union) {
		
		if (single.isEmpty()) {
			return union;
		}
		
		SortedCollection4 sorted = new SortedCollection4(BTreeRangeSingle.COMPARISON);
		sorted.add(single);
		
		Iterator4 ranges = union.ranges();
		while (ranges.moveNext()) {
			BTreeRangeSingle current = (BTreeRangeSingle) ranges.current();
			if (canBeMerged(current, single)) {
				sorted.remove(single);
				single = merge(current, single);
				sorted.add(single);
			} else {
				sorted.add(current);
			}
		}
		
		if (1 == sorted.size()) {
			//TODO: cleanup this mess
			return (BTreeRange)sorted.toArray(new Object[1])[0];
		}
		
		return new BTreeRangeUnion(sorted);
	}

	private static BTreeRange unionImpl(BTreeRangeSingle range1, BTreeRangeSingle range2) {
		if (range1.isEmpty()) {
			return range2;
		}
		if (range2.isEmpty()) {
			return range1;
		}
		if (canBeMerged(range1, range2)) {
			return merge(range1, range2);
		}
		return new BTreeRangeUnion(new BTreeRangeSingle[] { range1, range2 });
	}

	private static BTreeRangeSingle merge(BTreeRangeSingle range1, BTreeRangeSingle range2) {
		return range1.newBTreeRangeSingle(
					BTreePointer.min(range1.first(), range2.first()),
					BTreePointer.max(range1.end(), range2.end()));
	}

	private static boolean canBeMerged(BTreeRangeSingle range1, BTreeRangeSingle range2) {
		return range1.internalOverlaps(range2)
				|| range1.internalAdjacent(range2);
	}
}
