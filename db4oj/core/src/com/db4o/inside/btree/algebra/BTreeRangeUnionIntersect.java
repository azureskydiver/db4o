/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree.algebra;

import com.db4o.inside.btree.*;

/**
 * @exclude
 */
public class BTreeRangeUnionIntersect extends BTreeRangeUnionOperation {

	public BTreeRangeUnionIntersect(BTreeRangeUnion union) {
		super(union);
	}

	protected BTreeRange execute(BTreeRangeSingle range) {
		return BTreeAlgebra.intersect(_union, range);
	}

	protected BTreeRange execute(BTreeRangeUnion union) {
		return BTreeAlgebra.intersect(_union, union);
	}

}
