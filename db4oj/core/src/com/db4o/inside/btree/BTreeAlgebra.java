/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.ArgumentNullException;

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
		throw new com.db4o.foundation.NotImplementedException();
	}

	private static BTreeRange unionImpl(BTreeRangeSingle range1, BTreeRangeSingle range2) {
		if (range1.internalOverlaps(range2)
				|| range1.internalAdjacent(range2)) {
			return range1.newBTreeRangeSingle(
						BTreePointer.min(range1.first(), range2.first()),
						BTreePointer.max(range1.end(), range2.end()));
		}
		return new BTreeRangeUnion(new BTreeRangeSingle[] { range1, range2 });
	}
}
