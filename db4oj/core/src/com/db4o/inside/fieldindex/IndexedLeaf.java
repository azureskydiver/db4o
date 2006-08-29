/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.BTreeRange;

/**
 * @exclude
 */
public class IndexedLeaf extends IndexedNodeBase {
	
	private final BTreeRange _range;
    
    public IndexedLeaf(QConObject qcon) {
    	super(qcon);
    	_range = search();
    }
    
    private BTreeRange search() {
        
		final BTreeRange range = search(constraint().getObject());
        final QEBitmap bitmap = QEBitmap.forQE(constraint().i_evaluator);
        if (bitmap.takeGreater()) {        
            if (bitmap.takeEqual()) {
                return range.extendToLast();
            }
            return range.greater();
        }
        if (bitmap.takeSmaller()) {
        	if (bitmap.takeEqual()) {
        		return range.extendToFirst();
        	}
        	return range.smaller();
        }
        return range;
    }

	public int resultSize() {
        return _range.size();
    }

	// FIXME: do we need this?
    public TreeInt toTreeInt() {
    	return addRangeToTree(null, _range);
    }

	public KeyValueIterator iterator() {
		return _range.iterator();
	}

	public BTreeRange getRange() {
		return _range;
	}
}
