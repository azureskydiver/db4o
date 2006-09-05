/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree.algebra;

import com.db4o.inside.btree.BTreeRangeSingle;

/**
 * @exclude
 */
public abstract class BTreeRangeSingleOperation extends BTreeRangeOperation {

	protected final BTreeRangeSingle _single;

	public BTreeRangeSingleOperation(BTreeRangeSingle single) {
		_single = single;
	}

}