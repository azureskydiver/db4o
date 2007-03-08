/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class TreeNodeIterator extends AbstractTreeIterator {
	public TreeNodeIterator(Tree tree) {
		super(tree);
	}

	protected Object currentValue(Tree tree) {
		return tree.root();
	}
}
