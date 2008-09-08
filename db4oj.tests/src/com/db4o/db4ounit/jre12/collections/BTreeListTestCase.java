/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.collections.*;
import com.db4o.internal.collections.BTreeList.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class BTreeListTestCase extends AbstractDb4oTestCase implements OptOutNetworkingCS, OptOutDefragSolo {
	
	private static final int[] elements = {42, 43, 47, 49};
	
	private static final int FIRST_ELEMENT = elements[0];
	
	public void testMultipleElements(){
		BTreeList bTreeList = newBTreeList();
		for (int i = 0; i < 3; i++) {
			bTreeList.add(trans(), elements[i]);
		}
		for (int i = 0; i < 3; i++) {
			Assert.areEqual(elements[i], bTreeList.get(trans(), i));
		}
		
	}

	public void testOneElement(){
		BTreeList bTreeList = newBTreeList();
		bTreeList.add(trans(), FIRST_ELEMENT);
		BTree payload = bTreeList.payload();
		final int payloadId = payload.root().getID();
		assertOneElement(bTreeList, payloadId);
		Assert.areEqual(FIRST_ELEMENT, bTreeList.get(trans(), 0));
	}

	private void assertOneElement(BTreeList bTreeList, final int payloadId) {
		BTree payload = bTreeList.payload();
		Assert.areEqual(payloadId, payload.root().getID());
		Assert.areEqual(1, payload.size(trans()));
		payload.traverseKeys(trans(), new Visitor4() {
			public void visit(Object obj) {
				Assert.areEqual(FIRST_ELEMENT, obj);
			}
		});
		BTree index = bTreeList.index();
		Assert.areEqual(1, index.size(trans()));
		index.traverseKeys(trans(), new Visitor4() {
			public void visit(Object obj) {
				IndexEntry entry = (IndexEntry) obj;
				Assert.areEqual(payloadId, entry._nodeId);
			}
		});
	}
	
	public void testEmptyList(){
		BTreeList bTreeList = newBTreeList();
		int id = bTreeList.getID();
		Assert.isGreater(0, id);
		assertEmpty(bTreeList);
	}

	public void testPersistence() throws Exception{
		BTreeList bTreeList = newBTreeList();
		int id = bTreeList.getID();
		reopen();
		bTreeList = new BTreeList(trans(), id);
		Assert.areEqual(id, bTreeList.getID());
		assertEmpty(bTreeList);
		bTreeList.add(trans(), FIRST_ELEMENT);
		BTree payload = bTreeList.payload();
		final int payloadId = payload.root().getID();
		reopen();
		bTreeList = new BTreeList(trans(), id);
		assertOneElement(bTreeList, payloadId);
	}
	
	private void assertEmpty(BTreeList bTreeList) {
		BTree index = bTreeList.index();
		Assert.isNotNull(index);
		Assert.areEqual(0, index.size(trans()));
		BTree payload = bTreeList.payload();
		Assert.isNotNull(payload);
		Assert.areEqual(0, payload.size(trans()));
	}
	
	private BTreeList newBTreeList() {
		BTreeList bTreeList = new BTreeList(trans());
		bTreeList.write(trans());
		return bTreeList;
	}

	public static void main(String[] args) {
		new BTreeListTestCase().runAll();
	}
	
}
