/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.Transaction;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreeRangeImpl implements BTreeRange {
    
    private final Transaction _trans;
    
    private final BTreePointer _first;
    
    private final BTreePointer _end; 

    public BTreeRangeImpl(Transaction trans, BTreePointer first, BTreePointer end) {
        _trans = trans;
        _first = first;
        _end = end;
    }
    
    public int size() {
    	int size = 0;
		final KeyValueIterator i = iterator();
		while (i.moveNext()) {
			++size;
		}
		return size;
    }

	public KeyValueIterator iterator() {
		return new BTreeRangeIterator(this);
	}

    public final BTreePointer end() {
		return _end;
	}

	public Transaction transaction() {
		return _trans;
	}

	public BTreePointer first() {
        return _first;
    }

	public BTreeRange greater() {
		return new BTreeRangeImpl(_trans, _end, null);
	}
	
	public BTreeRange union(BTreeRange other) {
//		return new BTreeRangeImpl(
//					trans(),
//					min(_first, other._first),
//					max(_end, other._end));
		//return new BTreeRangeUnion(this, other);
		return null;
	}
	
	public BTreeRange extendToFirst() {
		return new BTreeRangeImpl(_trans, firstBTreePointer(), _end);
	}

	public BTreeRange extendToLast() {
		return new BTreeRangeImpl(_trans, _first, null);
	}

	public BTreeRange smaller() {
		return new BTreeRangeImpl(_trans, firstBTreePointer(), _first);
	}

	private BTreePointer firstBTreePointer() {
		return btree().firstPointer(_trans);
	}

	private BTree btree() {
		return _first.node().btree();
	}

	public BTreeRange intersect(BTreeRange range) {
		final BTreeRangeImpl rangeImpl = checkRangeArgument(range);
		BTreePointer first = max(_first, rangeImpl._first);
		BTreePointer end = min(_end, rangeImpl._end);
		return new BTreeRangeImpl(_trans, first, end);
	}

	private BTreePointer min(BTreePointer x, BTreePointer y) {
		return null;
	}

	private BTreePointer max(BTreePointer x, BTreePointer y) {
		return null;
	}

	public BTreeRange extendToLast(BTreeRange upperRange) {
		BTreeRangeImpl upper = checkRangeArgument(upperRange);
		return new BTreeRangeImpl(_trans, _first, upper._end);
	}

	private BTreeRangeImpl checkRangeArgument(BTreeRange range) {
		if (null == range) {
			throw new ArgumentNullException();
		}
		BTreeRangeImpl upper = (BTreeRangeImpl)range;
		if (btree() != upper.btree()) {
			throw new IllegalArgumentException();
		}
		return upper;
	}
}
