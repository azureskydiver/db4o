/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.ArrayList;
import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.drs.inside.TestableReplicationProviderInside;

import db4ounit.Assert;

public class ListTest extends DrsTestCase {

	public void test() {
		actualTest();
	}
	
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
		a().provider().storeNew(lh);
		a().provider().commit();

		ensureContent(a(), new String[]{"h1"}, new String[]{"c1", "c2"});
	}

	protected ListHolder createHolder() {
		ListHolder lh = new ListHolder("h1");
		lh.setList(new ArrayList());
		return lh;
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(a().provider(), b().provider());

		ensureContent(a(), new String[]{"h1"}, new String[]{"c1", "c2"});
		ensureContent(b(), new String[]{"h1"}, new String[]{"c1", "c2"});
	}

	private void modifyInProviderB() {

		ListHolder lh = (ListHolder) getOneInstance(b(), ListHolder.class);

		lh.setName("h2");
		ListContent lc1 = (ListContent) lh.getList().get(0);
		ListContent lc2 = (ListContent) lh.getList().get(1);
		lc1.setName("co1");
		lc2.setName("co2");

		b().provider().update(lc1);
		b().provider().update(lc2);
		b().provider().update(lh.getList());
		b().provider().update(lh);

		b().provider().commit();

		ensureContent(b(), new String[]{"h2"}, new String[]{"co1", "co2"});
	}

	private void replicateAllStep2() {
		replicateAll(b().provider(), a().provider());

		ensureContent(b(), new String[]{"h2"}, new String[]{"co1", "co2"});
		ensureContent(a(), new String[]{"h2"}, new String[]{"co1", "co2"});
	}

	private void addElementInProviderA() {

		ListHolder lh = (ListHolder) getOneInstance(a(), ListHolder.class);
		lh.setName("h3");
		ListContent lc3 = new ListContent("co3");
		a().provider().storeNew(lc3);
		lh.getList().add(lc3);

		a().provider().update(lh.getList());
		a().provider().update(lh);
		a().provider().commit();

		ensureContent(a(), new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
	}

	private void replicateHolderStep3() {
		replicateClass(a().provider(), b().provider(), ListHolder.class);

		ensureContent(a(), new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
		ensureContent(b(), new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
	}

	private void ensureContent(DrsFixture fixture, String[] holderNames, String[] contentNames) {
		int holderCount = holderNames.length;
		ensureInstanceCount(fixture, ListHolder.class, holderCount);

		// After dropping generating uuid for collection, it does not
		//  make sense to count collection because collection is never reused
		//	ensureInstanceCount(provider, ArrayList.class, holderCount);

		int i = 0;
		ObjectSet objectSet = fixture.provider().getStoredObjects(ListHolder.class);
		while (objectSet.hasNext()) {
			ListHolder lh = (ListHolder) objectSet.next();
			Assert.areEqual(holderNames[i], lh.getName());

			List list = lh.getList();
			for (int j = 0; j < contentNames.length; j++) {
				ListContent lc = (ListContent) list.get(j);
				final String name = lc.getName();
				Assert.areEqual(contentNames[j], name);
			}
		}
	}

}
