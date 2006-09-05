package com.db4o.inside.btree;

import com.db4o.foundation.*;

class BTreeRangeUnion implements BTreeRange {

	private final BTreeRangeSingle[] _ranges;

	public BTreeRangeUnion(BTreeRangeSingle[] ranges) {
		if (null == ranges) {
			throw new ArgumentNullException();
		}
		_ranges = toSortedArray(ranges);
	}

	private BTreeRangeSingle[] toSortedArray(BTreeRangeSingle[] ranges) {
		Comparison4 comparison = new Comparison4() {
			public int compare(Object x, Object y) {
				BTreeRangeSingle xRange = (BTreeRangeSingle)x;
				BTreeRangeSingle yRange = (BTreeRangeSingle)y;
				return xRange.first().compareTo(yRange.first());
			}
		};
		SortedCollection4 collection = new SortedCollection4(comparison);
		for (int i = 0; i < ranges.length; i++) {
			BTreeRangeSingle range = ranges[i];
			if (!range.isEmpty()) {
				collection.add(range);
			}
		}		
		return (BTreeRangeSingle[]) collection.toArray(new BTreeRangeSingle[collection.size()]);
	}

	public BTreeRange extendToFirst() {
		throw new NotImplementedException();
	}

	public BTreeRange extendToLast() {
		throw new NotImplementedException();
	}

	public BTreeRange extendToLastOf(BTreeRange upperRange) {
		throw new NotImplementedException();
	}

	public BTreeRange greater() {
		throw new NotImplementedException();
	}

	public BTreeRange intersect(BTreeRange range) {
		throw new NotImplementedException();
	}
	
	public Iterator4 iterator() {
		return Iterators.concat(Iterators.map(_ranges, new Function4() {
			public Object apply(Object range) {
				return ((BTreeRange)range).iterator();
			}
		}));
	}

	public Iterator4 keys() {
		return Iterators.concat(Iterators.map(_ranges, new Function4() {
			public Object apply(Object range) {
				return ((BTreeRange)range).keys();
			}
		}));
	}
	
	public int size() {
		int size = 0;
		for (int i = 0; i < _ranges.length; i++) {
			size += _ranges[i].size();
		}
		return size;
	}

	public BTreeRange smaller() {
		throw new NotImplementedException();
	}

	public BTreeRange union(BTreeRange other) {
		return BTreeAlgebra.union(this, other);
	}
}
