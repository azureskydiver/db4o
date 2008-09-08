/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.btree;

import com.db4o.internal.*;

/**
 * @exclude
 */
public interface BTreeStructureListener {

	public void notifySplit(Transaction trans, BTreeNode originalNode, BTreeNode newRightNode);

	void notifyDeleted(Transaction trans, BTreeNode node);

	void notifyCountChanged(Transaction trans, BTreeNode node, int diff);

}
