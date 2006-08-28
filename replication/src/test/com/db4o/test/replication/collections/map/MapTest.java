package com.db4o.test.replication.collections.map;

import java.util.HashMap;
import java.util.Map;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.Test;
import com.db4o.test.replication.ReplicationTestCase;

public class MapTest extends ReplicationTestCase {

	protected void actualTest() {

		storeMapToProviderA();

		replicateAllToProviderBFirstTime();

		//modifyInProviderB();

		//replicateAllStep2();

		//addElementInProviderA();

		//replicateHolderStep3();
	}
	
	private void storeMapToProviderA() {

		MapHolder mh = createMapHolder();
		MapContent mc1 = new MapContent("c1");
		MapContent mc2 = new MapContent("c2");
		mh.put(new MapKey("key1"), mc1);
		mh.put(new MapKey("key2"), mc2);
		_providerA.storeNew(mh);
		_providerA.commit();

		final MapKey[] keys = new MapKey[]{new MapKey("key1"), new MapKey("key2")};
		final MapContent[] values = new MapContent[]{new MapContent("c1"), new MapContent("c2")};
		ensureContent(_providerA, "h1", keys, 
				values);
	}

	private void ensureContent(TestableReplicationProviderInside provider,
			String holderName, MapKey[] keys, MapContent[] values) {
		ensureInstanceCount(provider, MapHolder.class, 1);

		ObjectSet objectSet = provider.getStoredObjects(MapHolder.class);

		MapHolder lh = (MapHolder) objectSet.next();
		Test.ensure(holderName.equals(lh.getName()));

		Map map = lh.getMap();
		
		ensureEquals(keys.length,map.size());
		
		for (MapKey key : keys)				
			ensure(map.containsKey(key), key + " not found in " + provider);
		
		for (MapContent value : values)
			ensure(map.containsValue(value), value + " not found in " + provider);
	}

	private void storeMapToProviderAOLD() {

		MapHolder mh = createMapHolder();
		MapContent mc1 = new MapContent("c1");
		MapContent mc2 = new MapContent("c2");
		mh.put("key1", mc1);
		mh.put("key2", mc2);
		_providerA.storeNew(mh);
		_providerA.commit();

		ensureContent(_providerA, new String[]{"h1"}, new String[]{"key1", "key2"}, new String[]{"c1", "c2"});
	}

	protected MapHolder createMapHolder() {
		MapHolder mh = new MapHolder("h1");
		mh.setMap(new HashMap());
		return mh;
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureContent(_providerA, "h1", new MapKey[]{new MapKey("key1"), new MapKey("key2")}, 
				new MapContent[]{new MapContent("c1"), new MapContent("c2")});
		
		ensureContent(_providerB, "h1", new MapKey[]{new MapKey("key1"), new MapKey("key2")}, 
				new MapContent[]{new MapContent("c1"), new MapContent("c2")});
	}

	private void modifyInProviderB() {

		MapHolder mh = (MapHolder) getOneInstance(_providerB, MapHolder.class);

		mh.setName("h2");
		MapContent mc1 = (MapContent) mh.getMap().get("key1");
		MapContent mc2 = (MapContent) mh.getMap().get("key2");
		mc1.name= "co1";
		mc2.name="co2";

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

		// After dropping generating uuid for collection, it does not
		//  make sense to count collection because collection is never reused
		// ensureInstanceCount(provider, Map.class, holderCount);

		int i = 0;
		ObjectSet objectSet = provider.getStoredObjects(MapHolder.class);
		while (objectSet.hasNext()) {
			MapHolder lh = (MapHolder) objectSet.next();
			Test.ensure(holderNames[i].equals(lh.getName()));

			Map Map = lh.getMap();
			for (int j = 0; j < keyNames.length; j++) {
				MapContent mc = (MapContent) Map.get(keyNames[j]);
				final String name = mc.name;
				Test.ensure(valueNames[j].equals(name));
			}
		}
	}

	public void test() {
		super.test();
	}
}
