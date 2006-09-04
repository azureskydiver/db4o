package com.db4o.inside.btree;

import com.db4o.foundation.*;

class BTreeRangeUnion implements BTreeRange {

	private final BTreeRangeSingle[] _ranges;

	public BTreeRangeUnion(BTreeRangeSingle[] ranges) {
		if (null == ranges) {
			throw new ArgumentNullException();
		}
		_ranges = ranges;
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
		return Iterators.cat(Iterators.map(_ranges, new Function() {
			public Object apply(Object range) {
				return ((BTreeRange)range).iterator();
			}
		}));
	}

	public Iterator4 keys() {
		return Iterators.cat(Iterators.map(_ranges, new Function() {
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
