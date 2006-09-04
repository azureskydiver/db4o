/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.Transaction;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreeRangeSingle implements BTreeRange {
    
    private final Transaction _transaction;

	private final BTree _btree;
	
	private final BTreePointer _first;
    
    private final BTreePointer _end;

    public BTreeRangeSingle(Transaction transaction, BTree btree, BTreePointer first, BTreePointer end) {
    	if (transaction == null || btree == null) {
    		throw new ArgumentNullException();
    	}
    	_transaction = transaction;
    	_btree = btree;
        _first = first;
        _end = end;
    }
    
    public int size() {
    	int size = 0;
		final Iterator4 i = keys();
		while (i.moveNext()) {
			++size;
		}
		return size;
    }
    
    public Iterator4 iterator() {
    	return new BTreeRangePointerIterator(this);
    }

	public Iterator4 keys() {
		return new BTreeRangeKeyIterator(this);
	}

    public final BTreePointer end() {
		return _end;
	}

	public Transaction transaction() {
		return _transaction;
	}

	BTreePointer first() {
        return _first;
    }

	public BTreeRange greater() {
		return newBTreeRangeSingle(_end, null);
	}
	
	public BTreeRange union(BTreeRange other) {
		return BTreeAlgebra.union(this, other);
	}
	
	boolean internalAdjacent(BTreeRangeSingle rangeImpl) {
		return BTreePointer.equals(_end, rangeImpl._first)
			|| BTreePointer.equals(rangeImpl._end, _first);
	}

	boolean internalOverlaps(BTreeRangeSingle y) {
		return firstOverlaps(this, y) || firstOverlaps(y, this);
	}

	private boolean firstOverlaps(BTreeRangeSingle x, BTreeRangeSingle y) {
		return BTreePointer.lessThan(y._first, x._end)
			&& BTreePointer.lessThan(x._first, y._end);
	}

	public BTreeRange extendToFirst() {
		return newBTreeRangeSingle(firstBTreePointer(), _end);
	}

	public BTreeRange extendToLast() {
		return newBTreeRangeSingle(_first, null);
	}

	public BTreeRange smaller() {
		return newBTreeRangeSingle(firstBTreePointer(), _first);
	}

	BTreeRange newBTreeRangeSingle(BTreePointer first, BTreePointer end) {
		return new BTreeRangeSingle(transaction(), _btree, first, end);
	}

	private BTreePointer firstBTreePointer() {
		return btree().firstPointer(transaction());
	}

	private BTree btree() {
		return _btree;
	}

	public BTreeRange intersect(BTreeRange range) {
		final BTreeRangeSingle rangeImpl = checkRangeArgument(range);
		BTreePointer first = BTreePointer.max(_first, rangeImpl._first);
		BTreePointer end = BTreePointer.min(_end, rangeImpl._end);
		return newBTreeRangeSingle(first, end);
	}

	public BTreeRange extendToLastOf(BTreeRange range) {
		BTreeRangeSingle rangeImpl = checkRangeArgument(range);
		return newBTreeRangeSingle(_first, rangeImpl._end);
	}
	
	public String toString() {
		return "BTreeRangeSingle(first=" + _first + ", end=" + _end + ")";
	}

	private BTreeRangeSingle checkRangeArgument(BTreeRange range) {
		if (null == range) {
			throw new ArgumentNullException();
		}
		BTreeRangeSingle rangeImpl = (BTreeRangeSingle)range;
		if (btree() != rangeImpl.btree()) {
			throw new IllegalArgumentException();
		}
		return rangeImpl;
	}
}
