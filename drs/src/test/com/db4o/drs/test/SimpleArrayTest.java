/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.Iterator;

import com.db4o.ObjectSet;
import com.db4o.drs.inside.TestableReplicationProviderInside;

import db4ounit.Assert;

public class SimpleArrayTest extends DrsTestCase {

	public void test() {

		storeListToProviderA();

		replicateAllToProviderBFirstTime();

		modifyInProviderB();

		replicateAllStep2();

		addElementInProviderA();

		replicateHolderStep3();
	}

	protected void clean() {delete(new Class[]{SimpleArrayHolder.class, SimpleArrayContent.class});}

	private void storeListToProviderA() {

		SimpleArrayHolder sah = new SimpleArrayHolder("h1");
		SimpleArrayContent sac1 = new SimpleArrayContent("c1");
		SimpleArrayContent sac2 = new SimpleArrayContent("c2");
		sah.add(sac1);
		sah.add(sac2);
		a().provider().storeNew(sah);
		a().provider().commit();

		ensureContent(a(), new String[]{"h1"}, new String[]{"c1", "c2"});
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(a().provider(), b().provider());

		ensureContent(a(), new String[]{"h1"}, new String[]{"c1", "c2"});
		ensureContent(b(), new String[]{"h1"}, new String[]{"c1", "c2"});
	}

	private void modifyInProviderB() {

		SimpleArrayHolder sah = (SimpleArrayHolder) getOneInstance(b(), SimpleArrayHolder.class);

		sah.setName("h2");
		SimpleArrayContent sac1 = sah.getArr()[0];
		SimpleArrayContent sac2 = sah.getArr()[1];
		sac1.setName("co1");
		sac2.setName("co2");

		b().provider().update(sac1);
		b().provider().update(sac2);
		b().provider().update(sah);

		b().provider().commit();

		ensureContent(b(), new String[]{"h2"}, new String[]{"co1", "co2"});
	}

	private void replicateAllStep2() {
		replicateAll(b().provider(), a().provider());

		ensureContent(b(), new String[]{"h2"}, new String[]{"co1", "co2"});
		ensureContent(a(), new String[]{"h2"}, new String[]{"co1", "co2"});
	}

	private void addElementInProviderA() {

		SimpleArrayHolder sah = (SimpleArrayHolder) getOneInstance(a(), SimpleArrayHolder.class);
		sah.setName("h3");
		SimpleArrayContent lc3 = new SimpleArrayContent("co3");
		a().provider().storeNew(lc3);
		sah.add(lc3);

		a().provider().update(sah);
		a().provider().commit();

		ensureContent(a(), new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
	}

	private void replicateHolderStep3() {
		replicateClass(a().provider(), b().provider(), SimpleArrayHolder.class);

		ensureContent(a(), new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
		ensureContent(b(), new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
	}

	private void ensureContent(DrsFixture fixture, String[] holderNames, String[] contentNames) {
		int holderCount = holderNames.length;
		int contentCount = contentNames.length;
		ensureInstanceCount(fixture, SimpleArrayHolder.class, holderCount);
		ensureInstanceCount(fixture, SimpleArrayContent.class, contentCount);

		int i = 0;
		ObjectSet objectSet = fixture.provider().getStoredObjects(SimpleArrayHolder.class);
		Iterator iterator = objectSet.iterator();
		while (iterator.hasNext()) {
			SimpleArrayHolder lh = (SimpleArrayHolder) iterator.next();
			Assert.areEqual(holderNames[i], lh.getName());
			//Test.ensure(holderNames[i].equals(lh.getName()));

			SimpleArrayContent[] sacs = lh.getArr();
			for (int j = 0; j < contentNames.length; j++) {
				Assert.areEqual(contentNames[j], sacs[j].getName());
				//Test.ensure(contentNames[j].equals(sacs[j].getName()));
			}
		}
	}

}
