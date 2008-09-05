/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.btree;

/**
 * @exclude
 */
public interface BTreeStructureListener {

	public void notifySplit(BTreeNode originalNode, BTreeNode newRightNode);

	void notifyDeleted(BTreeNode node);

}
