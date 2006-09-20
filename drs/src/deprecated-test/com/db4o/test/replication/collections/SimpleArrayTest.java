/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.collections;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.Test;
import com.db4o.test.replication.ReplicationTestCase;


public class SimpleArrayTest extends ReplicationTestCase {

	protected void actualTest() {

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
		_providerA.storeNew(sah);
		_providerA.commit();

		ensureContent(_providerA, new String[]{"h1"}, new String[]{"c1", "c2"});
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureContent(_providerA, new String[]{"h1"}, new String[]{"c1", "c2"});
		ensureContent(_providerB, new String[]{"h1"}, new String[]{"c1", "c2"});
	}

	private void modifyInProviderB() {

		SimpleArrayHolder sah = (SimpleArrayHolder) getOneInstance(_providerB, SimpleArrayHolder.class);

		sah.setName("h2");
		SimpleArrayContent sac1 = sah.getArr()[0];
		SimpleArrayContent sac2 = sah.getArr()[1];
		sac1.setName("co1");
		sac2.setName("co2");

		_providerB.update(sac1);
		_providerB.update(sac2);
		_providerB.update(sah);

		_providerB.commit();

		ensureContent(_providerB, new String[]{"h2"}, new String[]{"co1", "co2"});
	}

	private void replicateAllStep2() {
		replicateAll(_providerB, _providerA);

		ensureContent(_providerB, new String[]{"h2"}, new String[]{"co1", "co2"});
		ensureContent(_providerA, new String[]{"h2"}, new String[]{"co1", "co2"});
	}

	private void addElementInProviderA() {

		SimpleArrayHolder sah = (SimpleArrayHolder) getOneInstance(_providerA, SimpleArrayHolder.class);
		sah.setName("h3");
		SimpleArrayContent lc3 = new SimpleArrayContent("co3");
		_providerA.storeNew(lc3);
		sah.add(lc3);

		_providerA.update(sah);
		_providerA.commit();

		ensureContent(_providerA, new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
	}

	private void replicateHolderStep3() {
		replicateClass(_providerA, _providerB, SimpleArrayHolder.class);

		ensureContent(_providerA, new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
		ensureContent(_providerB, new String[]{"h3"}, new String[]{"co1", "co2", "co3"});
	}

	private void ensureContent(TestableReplicationProviderInside provider, String[] holderNames, String[] contentNames) {
		int holderCount = holderNames.length;
		int contentCount = contentNames.length;
		ensureInstanceCount(provider, SimpleArrayHolder.class, holderCount);
		ensureInstanceCount(provider, SimpleArrayContent.class, contentCount);

		int i = 0;
		ObjectSet objectSet = provider.getStoredObjects(SimpleArrayHolder.class);
		while (objectSet.hasNext()) {
			SimpleArrayHolder lh = (SimpleArrayHolder) objectSet.next();
			Test.ensure(holderNames[i].equals(lh.getName()));

			SimpleArrayContent[] sacs = lh.getArr();
			for (int j = 0; j < contentNames.length; j++) {
				Test.ensure(contentNames[j].equals(sacs[j].getName()));
			}
		}
	}

	public void test() {
		super.test();
	}
}
