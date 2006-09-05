/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree.algebra;

import com.db4o.inside.btree.*;

/**
 * @exclude
 */
public class BTreeRangeUnionUnion extends BTreeRangeUnionOperation {
	
	public BTreeRangeUnionUnion(BTreeRangeUnion union) {
		super(union);
	}

	protected BTreeRange execute(BTreeRangeUnion union) {
		return BTreeAlgebra.union(_union, union);
	}

	protected BTreeRange execute(BTreeRangeSingle single) {
		return BTreeAlgebra.union(_union, single);
	}
}
