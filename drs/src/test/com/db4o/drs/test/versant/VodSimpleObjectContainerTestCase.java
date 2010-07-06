/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import javax.jdo.*;

import com.db4o.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;

import db4ounit.*;

public class VodSimpleObjectContainerTestCase implements TestLifeCycle, ClassLevelFixtureTest {
	
	private static Class[] PERSISTENT_CLASSES = new Class[]{
		Chain.class,
		Holder.class,
		Item.class,
	};
	
	private static final String DATABASE_NAME = "SimpleObjectContainer";
	
	private VodDatabase _vod;	
	
	private VodReplicationProvider _provider;
	
	// This is a direct PersistenceManager that works around the _provider
	// so we can see what's committed, using a second reference system.
	private PersistenceManager _pm;
	
	public static void classSetUp() throws Exception {
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		vod.createDb();
		vod.amendPropertyIfNotExists("versant.metadata.0", "drs.jdo");
		vod.enhance("bin");
	}

	public static void classTearDown() {
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		vod.removeDb();
	}

	public void setUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.amendPropertyIfNotExists("versant.metadata.0", "drs.jdo");
		_provider = new VodReplicationProvider(_vod);
		_pm = _vod.createPersistenceManager();
		cleanDb();
	}

	public void tearDown() throws Exception {
		_pm.close();
		_provider.destroy();
		_vod = null;
	}
	
	private void cleanDb(){
		for (int i = 0; i < PERSISTENT_CLASSES.length; i++) {
			_provider.deleteAllInstances(PERSISTENT_CLASSES[i]);
		}
		_provider.commit();
	}
	
	public void testStoreNew(){
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		assertContent(Item.class, item);
	}
	
	public void testStoredSimpleObjects() {
		ObjectSet storedObjects = _provider.getStoredObjects(Item.class);
		Assert.areEqual(0, storedObjects.size());
		List<Item> items = new ArrayList<Item>();
		for (int i = 0; i < 3; i++) {
			Item item = new Item(String.valueOf(i));
			items.add(item);
			_provider.storeNew(item);
		}
		_provider.commit();
		
		storedObjects = _provider.getStoredObjects(Item.class);
		IteratorAssert.sameContent(items, storedObjects);
		assertContent(Item.class, items);
	}

	public void testStoredCompoundObjects() {
		List<Holder> holders = new ArrayList<Holder>();
		for (int i = 0; i < 3; i++) {
			Item item = new Item(String.valueOf(i));
			Item[] listItems = new Item[3]; 
			for (int j = 0; j < 3; j++) {
				listItems[j] = new Item(String.valueOf(i * j));
			}
			Holder holder = new Holder(item, listItems);
			holders.add(holder);
			_provider.storeNew(holder);
		}
		_provider.commit();
		ObjectSet storedObjects = _provider.getStoredObjects(Holder.class);
		Assert.areEqual(3, storedObjects.size());
		IteratorAssert.sameContent(holders, storedObjects);
	}
	
	public void testDelete(){
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		_provider.delete(item);
		_provider.commit();
		assertContent(Item.class);
	}
	
	public void testUpdate(){
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		assertContent(Item.class, item);
		item.name("updated");
		_provider.commit();
		assertContent(Item.class, item);
	}
	
	public void testDeleteAllInstances(){
		Item item1 = new Item("one");
		_provider.storeNew(item1);
		Item item2 = new Item("two");
		_provider.storeNew(item2);
		_provider.commit();
		assertContent(Item.class, item1, item2);
		_provider.deleteAllInstances(Item.class);
		_provider.commit();
		assertContent(Item.class);
	}
	
	public void testLongChain(){
		final int length = 1000;
		Chain chain = Chain.newChainWithLength(length);
		_provider.storeNew(chain);
		_provider.commit();
		transactional(new Runnable() {
			public void run() {
				Query query = _pm.newQuery(Chain.class, "this._id == 0");
				Collection collection = (Collection)query.execute();
				Chain chain = (Chain) collection.iterator().next();
				Assert.areEqual(length, chain.length());
			}
		});
	}
	
	
	
	private <T> void assertContent(Class<T> type, T...items) {
		assertContent(type, Arrays.asList(items));
	}
	
	private <T> void assertContent(final Class<T> type, final Iterable<T> items) {
		transactional(new Runnable() {
			public void run()  {
				Collection collection = (Collection) _pm.newQuery(type).execute();
				IteratorAssert.sameContent(items, collection);
			}
		});
	}
	
	private void transactional(Runnable runnable) {
		_pm.currentTransaction().begin();
		try{
			runnable.run();
		} finally {
			_pm.currentTransaction().rollback();
		}
	}

}
