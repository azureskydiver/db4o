package com.db4o.inside.btree;

import com.db4o.foundation.*;

class BTreeRangeUnion implements BTreeRange {

	private final BTreeRangeSingle[] _ranges;

	BTreeRangeUnion(BTreeRangeSingle[] ranges) {
		if (null == ranges) {
			throw new ArgumentNullException();
		}
		_ranges = toSortedArray(ranges);
	}

	BTreeRangeUnion(SortedCollection4 sorted) {
		if (null == sorted) {
			throw new ArgumentNullException();
		}
		_ranges = toArray(sorted);
	}
	
	public boolean isEmpty() {
		return false;
	}

	private BTreeRangeSingle[] toSortedArray(BTreeRangeSingle[] ranges) {		
		SortedCollection4 collection = new SortedCollection4(BTreeRangeSingle.COMPARISON);
		for (int i = 0; i < ranges.length; i++) {
			BTreeRangeSingle range = ranges[i];
			if (!range.isEmpty()) {
				collection.add(range);
			}
		}		
		return toArray(collection);
	}

	private BTreeRangeSingle[] toArray(SortedCollection4 collection) {
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
	
	public Iterator4 pointers() {
		return Iterators.concat(Iterators.map(_ranges, new Function4() {
			public Object apply(Object range) {
				return ((BTreeRange)range).pointers();
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

	public Iterator4 ranges() {
		return new ArrayIterator4(_ranges);
	}
}
