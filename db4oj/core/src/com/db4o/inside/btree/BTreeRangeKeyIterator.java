/**
 * 
 */
package com.db4o.inside.btree;


class BTreeRangeKeyIterator extends AbstractBTreeRangeIterator {
	
	public BTreeRangeKeyIterator(BTreeRangeSingle range) {
		super(range);
	}

	public Object current() {
		return currentPointer().key();
	}
}