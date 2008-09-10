/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.collections.*;
import com.db4o.internal.collections.BTreeList.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class BTreeListTestCase extends AbstractDb4oTestCase implements OptOutNetworkingCS, OptOutDefragSolo {
	
	private static final int FIRST_ELEMENT = 1;
	
	private static final int BTREE_NODE_SIZE = 7;
	
	private static final int[] createList(int length){
		int[] fibs = new int[length];
		fibs[0] = 1;
		fibs[1] = -1;
		for (int i = 2; i < fibs.length; i++) {
			int nextFib = Math.abs(fibs[i - 2]) + Math.abs(fibs[i -1]);
			if(i % 2 != 0){
				nextFib = -nextFib;  // alternate negative, so our list is not sorted.	
			}
			fibs[i] = nextFib;
		}
		return fibs;
	}
	
	protected void configure(Configuration config) throws Exception {
		config.bTreeNodeSize(BTREE_NODE_SIZE);
	}
	
	public void testPrepend(){
		BTreeList bTreeList = newBTreeList();
		int count = BTREE_NODE_SIZE - 3;
		addMultipleElements(bTreeList, count);
		bTreeList.add(trans(), 0, 0);
		Assert.areEqual(0, bTreeList.get(trans(), 0));
		assertMultipleElements(bTreeList, count, 1);
	}
	
	public void testMultipleElementsSpanningNodes(){
		BTreeList bTreeList = newBTreeList();
		int count = BTREE_NODE_SIZE * 5;
		addMultipleElements(bTreeList, count);
		assertMultipleElements(bTreeList, count);
	}
	
	public void testMultipleElementsInSingleNode(){
		BTreeList bTreeList = newBTreeList();
		int count = BTREE_NODE_SIZE - 2;
		addMultipleElements(bTreeList, count);
		assertMultipleElements(bTreeList, count);
	}

	private void addMultipleElements(BTreeList bTreeList, int elementCount) {
		int[] elements = createList(elementCount);
		for (int i = 0; i < elementCount; i++) {
			bTreeList.add(trans(), elements[i]);
		}
	}
	
	private void assertMultipleElements(BTreeList bTreeList, int elementCount) {
		assertMultipleElements(bTreeList, elementCount, 0);
	}

	private void assertMultipleElements(BTreeList bTreeList, int elementCount, int startIndex) {
		int[] elements = createList(elementCount);
		for (int i = 0; i < elementCount; i++) {
			Assert.areEqual(elements[i], bTreeList.get(trans(), startIndex + i));
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

	public void testOneElementPersistence() throws Exception{
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
	
	public void testMulitpleElementPersistence() throws Exception{
		BTreeList bTreeList = newBTreeList();
		int id = bTreeList.getID();
		int count = BTREE_NODE_SIZE - 2;
		addMultipleElements(bTreeList, count);
		reopen();
		bTreeList = new BTreeList(trans(), id);
		assertMultipleElements(bTreeList, count);
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
