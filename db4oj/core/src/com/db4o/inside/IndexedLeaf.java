/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.inside.btree.BTreeRange;


/**
 * @exclude
 */
public class IndexedLeaf extends IndexedNodeBase {
    
    public IndexedLeaf(Transaction transaction, QConObject qcon) {
    	super(transaction, qcon, null);
    	_range = search();
    }
    
    private BTreeRange search() {
        
		final BTreeRange range = search(_constraint.getObject());
        final QEBitmap bitmap = QEBitmap.forQE(_constraint.i_evaluator);
        if (bitmap.takeGreater()) {             
            if (bitmap.takeEqual()) {
                return range.extend();
            }
            return range.greater();
        }
        if (bitmap.takeSmaller()) {
            return range.smaller();
        }
        return range;
    }

	public int resultSize() {
        return _range.size();
    }

    public TreeInt toTreeInt() {
    	return addRangeToTree(null, _range);
    }

	public IndexedNode resolve() {
		if (isResolved()) {
			return null;
		}
		return parentNode();
	}

	private IndexedNode parentNode() {
		QCon parent = constraint().parent();
		if (parent instanceof QConObject) {
			return new IndexedPath(_transaction, (QConObject) parent, _range);
		}
		return null;
	}
}
