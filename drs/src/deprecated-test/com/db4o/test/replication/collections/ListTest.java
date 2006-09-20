/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.collections;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.Test;
import com.db4o.test.replication.ReplicationTestCase;

import java.util.ArrayList;
import java.util.List;

public class ListTest extends ReplicationTestCase {

	protected void actualTest() {

		storeListToProviderA();

		replicateAllToProviderBFirstTime();

		modifyInProviderB();

		replicateAllStep2();

		addElementInProviderA();

		replicateHolderStep3();
	}

	private void storeListToProviderA() {

		ListHolder lh = createHolder();
		ListContent lc1 = new ListContent("c1");
		ListContent lc2 = new ListContent("c2");
		lh.add(lc1);
		lh.add(lc2);
		_providerA.storeNew(lh);
		_providerA.commit();

		ensureContent(_providerA, new String[]{"h1"}, new String[]{"c1", "c2"});
	}

	protected ListHolder createHolder() {
		ListHolder lh = new ListHolder("h1");
		lh.setList(new ArrayList());
		return lh;
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureContent(_providerA, new String[]{"h1"}, new String[]{"c1", "c2"});
		ensureContent(_providerB, new String[]{"h1"}, new String[]{"c1", "c2"});
	}

	private void modifyInProviderB() {

		ListHolder lh = (ListHolder) getOneInstance(_providerB, ListHolder.class);

		lh.setName("h2");
		ListContent lc1 = (ListContent) lh.getList().get(0);
		ListContent lc2 = (ListContent) lh.getList().get(1);
		lc1.setName("co1");
		lc2.setName("co2");

		_providerB.update(lc1);
		_providerB.update(lc2);
		_providerB.update(lh.getList());
		_providerB.update(lh);

		_providerB.commit();

		ensureContent(_providerB, new String[]{"h2"}, new String[]{"co1", "co2"});
	}

	private void replicateAllStep2() {
		replicateAll(_providerB, _providerA);

		ensureContent(_providerB, new String[]{"h2"}, new String[]{"co1", "co2"});
		ensureContent(_providerA, new String[]{"h2"}, new String[]{"co1", "co2"});
	}

	private void addElementInProviderA() {

		ListHolder lh = (ListHolder) getOneInstance(_providerA, ListHolder.class);
		lh.setName("h3");
		ListContent lc3 = new ListContent("co3");
		_providerA.storeNew(lc3);
		lh.getList().add(lc3);

		_providerA.update(lh.getList());
		_providerA.update(lh);
		_providerA.commit();

		ensureContent(_providerA, new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
	}

	private void replicateHolderStep3() {
		replicateClass(_providerA, _providerB, ListHolder.class);

		ensureContent(_providerA, new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
		ensureContent(_providerB, new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
	}

	private void ensureContent(TestableReplicationProviderInside provider, String[] holderNames, String[] contentNames) {
		int holderCount = holderNames.length;
		ensureInstanceCount(provider, ListHolder.class, holderCount);

		// After dropping generating uuid for collection, it does not
		//  make sense to count collection because collection is never reused
		//	ensureInstanceCount(provider, ArrayList.class, holderCount);

		int i = 0;
		ObjectSet objectSet = provider.getStoredObjects(ListHolder.class);
		while (objectSet.hasNext()) {
			ListHolder lh = (ListHolder) objectSet.next();
			Test.ensure(holderNames[i].equals(lh.getName()));

			List list = lh.getList();
			for (int j = 0; j < contentNames.length; j++) {
				ListContent lc = (ListContent) list.get(j);
				final String name = lc.getName();
				Test.ensure(contentNames[j].equals(name));
			}
		}
	}


	public void test() {
		super.test();
	}
}
