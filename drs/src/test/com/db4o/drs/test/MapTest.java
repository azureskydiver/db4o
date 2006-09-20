/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.Map;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;

import db4ounit.Assert;


public class MapTest extends DrsTestCase {
	
	protected void actualTest() {

		storeMapToProviderA();

		replicateAllToProviderBFirstTime();

		modifyInProviderB();

		replicateAllStep2();

		addElementInProviderA();

		replicateHolderStep3();
	}

	private void storeMapToProviderA() {

		MapHolder mh = new MapHolder("h1");
		MapContent mc1 = new MapContent("c1");
		MapContent mc2 = new MapContent("c2");
		mh.put("key1", mc1);
		mh.put("key2", mc2);
		a().provider().storeNew(mh);
		a().provider().commit();

		ensureContent(a().provider(), new String[]{"h1"}, new String[]{"key1", "key2"}, new String[]{"c1", "c2"});
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(a().provider(), b().provider());

		ensureContent(a().provider(), new String[]{"h1"}, new String[]{"key1", "key2"}, new String[]{"c1", "c2"});
		ensureContent(b().provider(), new String[]{"h1"}, new String[]{"key1", "key2"}, new String[]{"c1", "c2"});
	}

	private void modifyInProviderB() {

		MapHolder mh = (MapHolder) getOneInstance(b().provider(), MapHolder.class);

		mh.setName("h2");
		MapContent mc1 = (MapContent) mh.getMap().get("key1");
		MapContent mc2 = (MapContent) mh.getMap().get("key2");
		mc1.setName("co1");
		mc2.setName("co2");

		b().provider().update(mc1);
		b().provider().update(mc2);
		b().provider().update(mh.getMap());
		b().provider().update(mh);

		b().provider().commit();

		ensureContent(b().provider(), new String[]{"h2"}, new String[]{"key1", "key2"}, new String[]{"co1", "co2"});
	}

	private void replicateAllStep2() {
		replicateAll(b().provider(), a().provider());

		ensureContent(a().provider(), new String[]{"h2"}, new String[]{"key1", "key2"}, new String[]{"co1", "co2"});
		ensureContent(b().provider(), new String[]{"h2"}, new String[]{"key1", "key2"}, new String[]{"co1", "co2"});
	}

	private void addElementInProviderA() {

		MapHolder mh = (MapHolder) getOneInstance(a().provider(), MapHolder.class);
		mh.setName("h3");
		MapContent mc3 = new MapContent("co3");
		a().provider().storeNew(mc3);
		mh.getMap().put("key3", mc3);

		a().provider().update(mh.getMap());
		a().provider().update(mh);
		a().provider().commit();

		ensureContent(a().provider(), new String[]{"h3"}, new String[]{"key1", "key2", "key3"}, new String[]{"co1", "co2", "co3"});
	}

	private void replicateHolderStep3() {
		replicateClass(a().provider(), b().provider(), MapHolder.class);

		ensureContent(a().provider(), new String[]{"h3"}, new String[]{"key1", "key2", "key3"}, new String[]{"co1", "co2", "co3"});
		ensureContent(b().provider(), new String[]{"h3"}, new String[]{"key1", "key2", "key3"}, new String[]{"co1", "co2", "co3"});
	}

	private void ensureContent(TestableReplicationProviderInside provider, String[] holderNames, String[] keyNames, String[] valueNames) {
		int holderCount = holderNames.length;
		ensureInstanceCount(provider, MapHolder.class, holderCount);

		// After dropping generating uuid for collection, it does not
		//  make sense to count collection because collection is never reused
		// ensureInstanceCount(provider, Map.class, holderCount);

		int i = 0;
		ObjectSet objectSet = provider.getStoredObjects(MapHolder.class);
		while (objectSet.hasNext()) {
			MapHolder lh = (MapHolder) objectSet.next();
			
			Assert.areEqual(holderNames[i], lh.getName());

			Map Map = lh.getMap();
			for (int j = 0; j < keyNames.length; j++) {
				MapContent mc = (MapContent) Map.get(keyNames[j]);
				final String name = mc.getName();
				Assert.areEqual(valueNames[j], name);
			}
		}
	}

	public void test() {
		actualTest();
	}

}
