/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.other;

import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.types.Db4oList;


public class Db4oListTest extends ListTest {

	public void test() {
		if (!(a().provider() instanceof Db4oReplicationProvider))
			return;

		super.actualTest();
	}

	protected ListHolder createHolder() {
		ListHolder lh = new ListHolder("h1");
		Db4oList list = ((Db4oReplicationProvider) a().provider()).getObjectContainer().collections().newLinkedList();

		lh.setList(list);
		return lh;
	}

}
