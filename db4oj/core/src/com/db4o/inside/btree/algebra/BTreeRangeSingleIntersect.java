/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree.algebra;

import com.db4o.inside.btree.*;

/**
 * @exclude
 */
public class BTreeRangeSingleIntersect extends BTreeRangeSingleOperation {

	public BTreeRangeSingleIntersect(BTreeRangeSingle single) {
		super(single);
	}

	protected BTreeRange execute(BTreeRangeSingle single) {
		return BTreeAlgebra.intersect(_single, single);
	}
	
	protected BTreeRange execute(BTreeRangeUnion union) {
		return BTreeAlgebra.intersect(union, _single);
	}
}
