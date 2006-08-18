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

}
