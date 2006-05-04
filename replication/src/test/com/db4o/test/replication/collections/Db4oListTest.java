package com.db4o.test.replication.collections;

import com.db4o.ObjectSet;
import com.db4o.types.Db4oList;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateReplicationProvider;
import com.db4o.test.Test;

import java.util.ArrayList;
import java.util.List;

public class Db4oListTest extends ListTest {

	protected void actualTest() {
		if (!(_providerA instanceof Db4oReplicationProvider))
			return;

		super.actualTest();
	}

	public void test() {
		super.test();
	}

	protected ListHolder createHolder() {
		ListHolder lh = new ListHolder("h1");
		Db4oList list = ((Db4oReplicationProvider) _providerA).getObjectContainer().collections().newLinkedList();

		lh.setList(list);
		return lh;
	}
}
