/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.collections.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class BTreeListTestCase extends AbstractDb4oTestCase implements OptOutNetworkingCS {
	
	public void _testOneElement(){
		BTreeList bTreeList = newBTreeList();
		final Integer value = new Integer(42);
		bTreeList.add(trans(), value);
		BTree payload = bTreeList.payload();
		Assert.areEqual(1, payload.size(trans()));
		final int payloadId = payload.getID();
		payload.traverseKeys(trans(), new Visitor4() {
			public void visit(Object obj) {
				Assert.areEqual(value, obj);
			}
		});
		BTree index = bTreeList.index();
		Assert.areEqual(1, index.size(trans()));
		index.traverseKeys(trans(), new Visitor4() {
			public void visit(Object obj) {
				int id = ((Integer)obj).intValue();
				Assert.areEqual(payloadId, id);
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

	
	
}
