/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree.algebra;

import com.db4o.inside.btree.BTreeRangeUnion;

/**
 * @exclude
 */
public abstract class BTreeRangeUnionOperation extends BTreeRangeOperation {

	protected final BTreeRangeUnion _union;

	public BTreeRangeUnionOperation(BTreeRangeUnion union) {
		_union = union;
	}

}