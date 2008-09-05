/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

import com.db4o.foundation.*;
import com.db4o.internal.btree.*;

import db4ounit.*;

/**
 * @exclude
 */
public class BTreeStructureChangeListenerTestCase extends BTreeTestCaseBase {
	
	public void testSplits(){
		final BooleanByRef splitNotified = new BooleanByRef(); 
		BTreeStructureListener listener = new BTreeStructureListener(){
			public void notifySplit(BTreeNode originalNode, BTreeNode newRightNode){
				Assert.isFalse(splitNotified.value);
				splitNotified.value = true;
			}
			public void notifyDeleted(BTreeNode node){
				
			}
		};
		_btree.structureListener(listener);
		for (int i = 0; i < BTREE_NODE_SIZE + 1; i++) {
			add(i);	
		}
		Assert.isTrue(splitNotified.value);
	}
	
	public void testDelete(){
		final IntByRef deletedCount = new IntByRef(); 
		BTreeStructureListener listener = new BTreeStructureListener(){
			public void notifySplit(BTreeNode originalNode, BTreeNode newRightNode){
				
			}
			public void notifyDeleted(BTreeNode node){
				deletedCount.value++;
			}
		};
		_btree.structureListener(listener);
		for (int i = 0; i < BTREE_NODE_SIZE + 1; i++) {
			add(i);	
		}
		
		for (int i = 0; i < BTREE_NODE_SIZE + 1; i++) {
			remove(i);	
		}
		Assert.areEqual(2, deletedCount.value);
	}


}
