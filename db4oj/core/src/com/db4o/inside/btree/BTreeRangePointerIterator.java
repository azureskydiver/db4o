package com.db4o.inside.btree;

public class BTreeRangePointerIterator extends AbstractBTreeRangeIterator {
	
	public BTreeRangePointerIterator(BTreeRangeSingle range) {
		super(range);
	}

	public Object current() {
		return currentPointer();
	}
}
