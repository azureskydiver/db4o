package com.db4o.test.replication.collections.map;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.Test;
import com.db4o.test.replication.ReplicationTestCase;

import java.util.Map;

public abstract class MapTest extends ReplicationTestCase {

	public void test() {


		delete(new Class[]{MapContent.class, MapHolder.class, Map.class});

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
		_providerA.storeNew(mh);
		_providerA.commit();

		ensureContent(_providerA, new String[]{"h1"}, new String[]{"key1", "key2"}, new String[]{"c1", "c2"});
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureContent(_providerA, new String[]{"h1"}, new String[]{"key1", "key2"}, new String[]{"c1", "c2"});
		ensureContent(_providerB, new String[]{"h1"}, new String[]{"key1", "key2"}, new String[]{"c1", "c2"});
	}

	private void modifyInProviderB() {

		MapHolder mh = (MapHolder) getOneInstance(_providerB, MapHolder.class);

		mh.setName("h2");
		MapContent mc1 = (MapContent) mh.getMap().get("key1");
		MapContent mc2 = (MapContent) mh.getMap().get("key2");
		mc1.setName("co1");
		mc2.setName("co2");

		_providerB.update(mc1);
		_providerB.update(mc2);
		_providerB.update(mh.getMap());
		_providerB.update(mh);

		_providerB.commit();

		ensureContent(_providerB, new String[]{"h2"}, new String[]{"key1", "key2"}, new String[]{"co1", "co2"});
	}

	private void replicateAllStep2() {
		replicateAll(_providerB, _providerA);

		ensureContent(_providerA, new String[]{"h2"}, new String[]{"key1", "key2"}, new String[]{"co1", "co2"});
		ensureContent(_providerB, new String[]{"h2"}, new String[]{"key1", "key2"}, new String[]{"co1", "co2"});
	}

	private void addElementInProviderA() {

		MapHolder mh = (MapHolder) getOneInstance(_providerA, MapHolder.class);
		mh.setName("h3");
		MapContent mc3 = new MapContent("co3");
		_providerA.storeNew(mc3);
		mh.getMap().put("key3", mc3);

		_providerA.update(mh.getMap());
		_providerA.update(mh);
		_providerA.commit();

		ensureContent(_providerA, new String[]{"h3"}, new String[]{"key1", "key2", "key3"}, new String[]{"co1", "co2", "co3"});
	}

	private void replicateHolderStep3() {
		replicateClass(_providerA, _providerB, MapHolder.class);

		ensureContent(_providerA, new String[]{"h3"}, new String[]{"key1", "key2", "key3"}, new String[]{"co1", "co2", "co3"});
		ensureContent(_providerB, new String[]{"h3"}, new String[]{"key1", "key2", "key3"}, new String[]{"co1", "co2", "co3"});
	}

	private void ensureContent(TestableReplicationProviderInside provider, String[] holderNames, String[] keyNames, String[] valueNames) {
		int holderCount = holderNames.length;
		ensureInstanceCount(provider, MapHolder.class, holderCount);

		int i = 0;
		ObjectSet objectSet = provider.getStoredObjects(MapHolder.class);
		while (objectSet.hasNext()) {
			MapHolder lh = (MapHolder) objectSet.next();
			Test.ensure(holderNames[i].equals(lh.getName()));

			Map Map = lh.getMap();
			for (int j = 0; j < keyNames.length; j++) {
				MapContent mc = (MapContent) Map.get(keyNames[j]);
				final String name = mc.getName();
				Test.ensure(valueNames[j].equals(name));
			}
		}
	}
}
