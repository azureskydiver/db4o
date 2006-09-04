package com.db4o.inside.btree;

import com.db4o.foundation.*;

public class BTreeRangeUnion implements BTreeRange {

	private BTreeRange _first;
	private BTreeRange _second;

	public BTreeRangeUnion(BTreeRange first, BTreeRange second) {
		if (null == first || null == second) {
			throw new ArgumentNullException();
		}
		_first = first;
		_second = second;
	}

	public BTreeRange extendToFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	public BTreeRange extendToLast() {
		// TODO Auto-generated method stub
		return null;
	}

	public BTreeRange extendToLastOf(BTreeRange upperRange) {
		// TODO Auto-generated method stub
		return null;
	}

	public BTreePointer first() {
		// TODO Auto-generated method stub
		return null;
	}

	public BTreeRange greater() {
		// TODO Auto-generated method stub
		return null;
	}

	public BTreeRange intersect(BTreeRange range) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Iterator4 iterator() {
		return new CompositeIterator4(new Iterator4[] { _first.iterator(), _second.iterator() });
	}

	public Iterator4 keys() {
		return new CompositeIterator4(new Iterator4[] { _first.keys(), _second.keys() });
	}

	public boolean overlaps(BTreeRange range) {
		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		return _first.size() + _second.size();
	}

	public BTreeRange smaller() {
		// TODO Auto-generated method stub
		return null;
	}

	public BTreeRange union(BTreeRange other) {
		// TODO Auto-generated method stub
		return null;
	}

}
