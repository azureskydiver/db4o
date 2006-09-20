package com.db4o.test.replication.collections.map;

import java.util.HashMap;
import java.util.Map;

import com.db4o.ObjectSet;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.test.Test;
import com.db4o.test.replication.ReplicationTestCase;

public class MapTest extends ReplicationTestCase {
	static final MapKey[] EXP_2_KEYS = new MapKey[]{new MapKey("key1"), new MapKey("key2")};
	
	static final MapContent[] EXP_2_VALUES = new MapContent[]{new MapContent("c1"), new MapContent("c2")};
	static final MapContent[] EXP_2_VALUES_UPDATED = new MapContent[]{new MapContent("co1"), new MapContent("co2")};
	
	static final MapKey[] EXP_3_KEYS = new MapKey[]{new MapKey("key1"), new MapKey("key2"), new MapKey("key3")};
	static final MapContent[] EXP_3_VALUES = new MapContent[]{new MapContent("co1"),new MapContent("co2"), new MapContent("co3")};
	
	protected void actualTest() {

		storeMapToProviderA();

		replicateAllToProviderBFirstTime();

		modifyInProviderB();

		replicateAllStep2();

		addElementInProviderA();

		replicateHolderStep3();
	}
	
	protected MapHolder createMapHolder() {
		MapHolder mh = new MapHolder("h1");
		mh.setMap(new HashMap());
		return mh;
	}
	
	private void storeMapToProviderA() {
		newStore();
	}

	private void oldStore() {
		MapHolder mh = createMapHolder();
		
		MapContent mc1 = new MapContent("c1");
		MapContent mc2 = new MapContent("c2");
		final MapKey key1 = new MapKey("key1");
		_providerA.storeNew(key1);
		mh.put(key1, mc1);
		
		mh.put(new MapKey("key2"), mc2);
		_providerA.storeNew(mh);
		_providerA.commit();

		ensureContent(_providerA, "h1", EXP_2_KEYS, EXP_2_VALUES);
	}

	private void newStore() {
		final MapKey key1 = new MapKey("key1");
		_providerA.storeNew(key1);
		
		final MapKey key2 = new MapKey("key2");
		_providerA.storeNew(key2);
		
		MapContent c1 = new MapContent("c1");
		_providerA.storeNew(c1);
		
		MapContent c2 = new MapContent("c2");
		_providerA.storeNew(c2);

		MapHolder mh = createMapHolder();
		mh.put(key1, c1);
		mh.put(key2, c2);

		_providerA.storeNew(mh);
		_providerA.commit();

		ensureContent(_providerA, "h1", EXP_2_KEYS, EXP_2_VALUES);
	}

	private void replicateAllToProviderBFirstTime() {
		//replicateAll(_providerA, _providerB);
		//replicateClass(_providerA, _providerB, MapKey.class);
		replicateClasses(_providerA, _providerB, new Class[]{MapKey.class, MapContent.class, MapHolder.class});
				
		ensureContent(_providerA, "h1", EXP_2_KEYS, EXP_2_VALUES);
		ensureContent(_providerB, "h1", EXP_2_KEYS, EXP_2_VALUES);
	}

	

	private void modifyInProviderB() {
		MapHolder mh = (MapHolder) getOneInstance(_providerB, MapHolder.class);

		mh.setName("h2");
		MapContent mc1 = (MapContent) mh.getMap().get(new MapKey("key1"));
		MapContent mc2 = (MapContent) mh.getMap().get(new MapKey("key2"));
		mc1.name= "co1";
		mc2.name= "co2";

		_providerB.update(mc1);
		_providerB.update(mc2);
		
		_providerB.update(mh.getMap());
		_providerB.update(mh);

		_providerB.commit();

		
		ensureContent(_providerB, "h2", EXP_2_KEYS, EXP_2_VALUES_UPDATED);
	}

	private void replicateAllStep2() {
		replicateAll(_providerB, _providerA);
		
		ensureContent(_providerA, "h2", EXP_2_KEYS, EXP_2_VALUES_UPDATED);
		ensureContent(_providerB, "h2", EXP_2_KEYS, EXP_2_VALUES_UPDATED);
	}

	private void addElementInProviderA() {
		MapHolder mh = (MapHolder) getOneInstance(_providerA, MapHolder.class);
		mh.setName("h3");
		
		final MapKey key3 = new MapKey("key3");
		_providerA.storeNew(key3);
		
		MapContent mc3 = new MapContent("co3");
		_providerA.storeNew(mc3);
		
		mh.getMap().put(key3, mc3);

		_providerA.update(mh.getMap());
		_providerA.update(mh);
		_providerA.commit();

		ensureContent(_providerA, "h3", EXP_3_KEYS, EXP_3_VALUES);
	}

	private void replicateHolderStep3() {
		replicateClasses(_providerA, _providerB, new Class[]{MapKey.class, MapHolder.class});
		
		ensureContent(_providerA, "h3", EXP_3_KEYS, EXP_3_VALUES);
		ensureContent(_providerB, "h3", EXP_3_KEYS, EXP_3_VALUES);
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
		
		for (int i = 0; i < keys.length; i++)
			ensureEquals(values[i], map.get(keys[i]));
	}

	public void test() {
		super.test();
	}
}
