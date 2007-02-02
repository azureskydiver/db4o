/**
 * 
 */
package com.db4o.internal.btree;


class BTreeRangeKeyIterator extends AbstractBTreeRangeIterator {
	
	public BTreeRangeKeyIterator(BTreeRangeSingle range) {
		super(range);
	}

	public Object current() {
		return currentPointer().key();
	}
}