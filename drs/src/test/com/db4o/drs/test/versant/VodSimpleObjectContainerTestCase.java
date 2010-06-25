/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import javax.jdo.*;

import com.db4o.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;

import db4ounit.*;

public class VodSimpleObjectContainerTestCase implements TestLifeCycle {
	
	private static final String DATABASE_NAME = "SimpleObjectContainer";
	
	private VodDatabase _vod;	
	
	private VodReplicationProvider _provider;
	
	// This is a direct PersistenceManager that works around the _provider
	// so we can see what's committed, using a second reference system.
	private PersistenceManager _pm;
	
	
	public void setUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.createDb();
		_vod.amendPropertyIfNotExists("versant.metadata.0", "drs.jdo");
		_vod.enhance("bin");
		_provider = new VodReplicationProvider(_vod);
		_pm = _vod.createPersistenceManager();
	}

	public void tearDown() throws Exception {
		_pm.close();
		
		_provider.destroy();
		_vod.removeDb();
		_vod = null;
	}
	
	private void cleanDb(){
		_provider.deleteAllInstances(Item.class);
		_provider.deleteAllInstances(Holder.class);
		_provider.commit();
	}
	
	public void testAll(){
		tstStoreNew();
		cleanDb();
		tstStoredSimpleObjects();
		cleanDb();
		tstStoredCompoundObjects();
		cleanDb();
		tstDelete();
		cleanDb();
		tstUpdate();
		cleanDb();
	}
	
	public void tstStoreNew(){
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		assertContent(Item.class, item);
	}
	
	public void tstStoredSimpleObjects() {
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

	public void tstStoredCompoundObjects() {
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
	
	public void tstDelete(){
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		_provider.delete(item);
		_provider.commit();
		assertContent(Item.class);
	}
	
	public void tstUpdate(){
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		assertContent(Item.class, item);
		item.name("updated");
		_provider.commit();
		assertContent(Item.class, item);
	}
	
	private <T> void assertContent(Class<T> type, T...items) {
		assertContent(type, Arrays.asList(items));
	}
	
	private <T> void assertContent(Class<T> type, Iterable<T> items) {
		_pm.currentTransaction().begin();
		Collection collection = (Collection) _pm.newQuery(type).execute();
		IteratorAssert.sameContent(items, collection);
		_pm.currentTransaction().rollback();
	}

}
