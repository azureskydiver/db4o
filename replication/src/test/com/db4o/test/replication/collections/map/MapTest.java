package com.db4o.test.replication.collections.map;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateReplicationProvider;
import com.db4o.test.Test;
import com.db4o.test.replication.ReplicationTestCase;
import com.db4o.types.Db4oMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapTest extends ReplicationTestCase {
	final MapKey key1 = new MapKey("key1");
	final MapKey key2 = new MapKey("key2");
	final MapKey key3 = new MapKey("key3");
	
	final MapKey[] twoKeys = new MapKey[]{key1, key2}; 
	final MapKey[] threeKeys = new MapKey[]{key1, key2, key3}; 
	
	protected void actualTest() {

		storeMapToProviderA();

		replicateAllToProviderBFirstTime();

		modifyInProviderB();

		replicateAllStep2();

		addElementInProviderA();

		replicateHolderStep3();
	}
	
	protected MapHolder createMapHolder() {
		return new MapHolder("h1");
	}

	private void storeMapToProviderA() {
		MapHolder mh = createMapHolder();
		MapContent mc1 = new MapContent("c1");
		MapContent mc2 = new MapContent("c2");
		
		_providerA.storeNew(key1);
		_providerA.storeNew(key2);
		
		mh.put(key1, mc1);
		mh.put(key2, mc2);
		
		_providerA.storeNew(mh);
		_providerA.commit();
		
		ensureContent(_providerA, new String[]{"h1"}, twoKeys, new String[]{"c1", "c2"});
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureContent(_providerA, new String[]{"h1"}, twoKeys, new String[]{"c1", "c2"});
		ensureContent(_providerB, new String[]{"h1"}, twoKeys, new String[]{"c1", "c2"});
	}

	private void modifyInProviderB() {

		MapHolder mh = (MapHolder) getOneInstance(_providerB, MapHolder.class);

		mh.setName("h2");
		MapContent mc1 = (MapContent) mh.getMap().get(key1);
		MapContent mc2 = (MapContent) mh.getMap().get(key2);
		mc1.setName("co1");
		mc2.setName("co2");

		_providerB.update(mc1);
		_providerB.update(mc2);
		_providerB.update(mh.getMap());
		_providerB.update(mh);

		_providerB.commit();

		ensureContent(_providerB, new String[]{"h2"}, twoKeys, new String[]{"co1", "co2"});
	}

	private void replicateAllStep2() {
		replicateAll(_providerB, _providerA);

		ensureContent(_providerA, new String[]{"h2"}, twoKeys, new String[]{"co1", "co2"});
		ensureContent(_providerB, new String[]{"h2"}, twoKeys, new String[]{"co1", "co2"});
	}

	private void addElementInProviderA() {

		MapHolder mh = (MapHolder) getOneInstance(_providerA, MapHolder.class);
		mh.setName("h3");
		MapContent mc3 = new MapContent("co3");
		
		_providerA.storeNew(key3);
		_providerA.storeNew(mc3);
		
		mh.getMap().put(key3, mc3);

		_providerA.update(mh.getMap());
		_providerA.update(mh);
		_providerA.commit();

		ensureContent(_providerA, new String[]{"h3"}, threeKeys, new String[]{"co1", "co2", "co3"});
	}

	private void replicateHolderStep3() {
		replicateClass(_providerA, _providerB, MapKey.class);
		replicateClass(_providerA, _providerB, MapHolder.class);

		ensureContent(_providerA, new String[]{"h3"}, threeKeys, new String[]{"co1", "co2", "co3"});
		ensureContent(_providerB, new String[]{"h3"}, threeKeys, new String[]{"co1", "co2", "co3"});
	}

	private void ensureContent(TestableReplicationProviderInside provider, String[] holderNames, MapKey[] keys, String[] valueNames) {
		System.out.println("ensureContent()");
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

			Map map = lh.getMap();
			
			Test.ensureEquals(map.size(), keys.length);
			
			final Iterator itor = map.entrySet().iterator();
			while(itor.hasNext()){
				final Map.Entry entry = (Entry) itor.next();
				final Object key = entry.getKey();
				final Object value = entry.getValue();
				
				System.out.println("key = " + key);
				System.out.println("value = " + value);
			}
			
			System.out.println("end dumping map content");
				
			for (int j = 0; j < keys.length; j++) {
				final MapKey key = keys[j];
				System.out.println("key = " + key);
				
				Test.ensure(map.containsKey(key));
				
				MapContent mc = (MapContent) map.get(key);
				
				Test.ensure(mc!=null);
				
				final String name = mc.getName();
				Test.ensure(valueNames[j].equals(name));
			}
		}
	}

	public void test() {
		super.test();
	}
}
