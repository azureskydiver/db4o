/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.btree.*;

import db4ounit.*;


public class BTreeNodeTestCase extends BTreeTestCaseBase {
	
	public static void main(String[] args) {
		new BTreeNodeTestCase().runSolo();
	}

	private final int[] keys = new int[] {
			-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 7, 9
	};
	
	protected void db4oSetupAfterStore() throws Exception {
		super.db4oSetupAfterStore();
		add(keys);		
		commit();
	}
	
	public void testLastKeyIndex(){
		BTreeNode node = node(3);
		Assert.areEqual(1, node.lastKeyIndex(trans()));
		Transaction trans = newTransaction();
		_btree.add(trans, new Integer(5));
		Assert.areEqual(1, node.lastKeyIndex(trans()));
		_btree.commit(trans);
		Assert.areEqual(2, node.lastKeyIndex(trans()));
	}

	private BTreeNode node(final int value) {
		BTreeRange range = search(value);
		Iterator4 i = range.pointers();
		i.moveNext();
		BTreePointer firstPointer = (BTreePointer) i.current();
		BTreeNode node = firstPointer.node();
		node.debugLoadFully(systemTrans());
		return node;
	}
	
	public void testLastPointer(){
		BTreeNode node = node(3);
		BTreePointer lastPointer = node.lastPointer(trans());
		assertPointerKey(4, lastPointer);
	}
	


}
