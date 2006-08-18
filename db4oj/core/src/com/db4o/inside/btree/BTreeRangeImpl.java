/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.Transaction;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreeRangeImpl implements BTreeRange {
    
    private final Transaction _trans;
    
    private final BTreePointer _start;
    
    private final BTreePointer _end; 

    public BTreeRangeImpl(Transaction trans, BTreePointer start, BTreePointer end) {
        _trans = trans;
        _start = start;
        _end = end;
    }
    
    public void traverseKeys(Visitor4 visitor) {
    	KeyValueIterator iterator = iterator();
    	while (iterator.moveNext()) {
    		visitor.visit(iterator.key());
    	}      
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

	public BTreePointer start() {
        return _start;
    }

}
