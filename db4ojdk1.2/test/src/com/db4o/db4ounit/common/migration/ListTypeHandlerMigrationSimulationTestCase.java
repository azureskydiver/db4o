/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.migration;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;

/**
 * @exclude
 */
public class ListTypeHandlerMigrationSimulationTestCase implements TestLifeCycle {
	
	public static class Item {
		
		public List list;
		
	}
	
	private String _fileName;
	
	boolean _useListTypeHandler;
	
	public void setUp() throws Exception {
		_fileName = Path4.getTempFileName();
		File4.delete(_fileName);
	}

	public void tearDown() throws Exception {
		File4.delete(_fileName);
	}
	
	public void testMigration(){
		
		if(TypeHandlerConfiguration.enabled()){
			// Then we always have a list Typehandler installed.
			// This test no longer makes sense.
			return;
		}
		
		_useListTypeHandler = false;
		
		storeItemWithListElement("one");
		storeItemWithListElement("two");
		storeItemWithListElement("three");
		storeItemWithListElement(new Integer(42));
		
		assertSingleItemElementQuery("one");
		assertNoItemFoundByElement("four");
		
		_useListTypeHandler = true;
		
		assertSingleItemElementQuery("one");
		assertSingleItemElementQuery(new Integer(42));
		
		updateItemByListElement("one", "newOne");
		
		assertNoItemFoundByElement("one");
		assertSingleItemElementQuery("two");
		
	}
	
	private void assertSingleItemElementQuery(Object element) {
		ObjectContainer db = openContainer();
		try{
			Item item = retrieveItemByElement(element, db);
			Object listElement = item.list.get(0);
			Assert.areEqual(element, listElement);
		} finally {
			db.close();
		}
	}

	private Item retrieveItemByElement(Object element, ObjectContainer db) {
		Query q = db.query();
		q.constrain(Item.class);
		q.descend("list").constrain(element);
		ObjectSet objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		Item item = (Item) objectSet.next();
		return item;
	}
	
	private void assertNoItemFoundByElement(Object element) {
		ObjectContainer db = openContainer();
		try{
			Query q = db.query();
			q.constrain(Item.class);
			q.descend("list").constrain(element);
			ObjectSet objectSet = q.execute();
			Assert.areEqual(0, objectSet.size());
		} finally {
			db.close();
		}
	}
	
	private void updateItemByListElement(Object oldElement, Object newElement) {
		ObjectContainer db = openContainer();
		try{
			Item item = retrieveItemByElement(oldElement, db);
			item.list.clear();
			item.list.add(newElement);
			db.store(item.list);
			db.store(item);
		} finally {
			db.close();
		}
	}
	
	private void storeItemWithListElement(Object element) {
		Item item = new Item();
		item.list = new ArrayList();
		item.list.add(element);
		ObjectContainer db = openContainer();
		try{
			db.store(item);
		} finally {
			db.close();
		}
	}

	private void store(Item item) {
		ObjectContainer db = openContainer();
		try{
			db.store(item);
		} finally {
			db.close();
		}
	}
	
	private void updateItem() {
		ObjectContainer db = openContainer();
		try {
		ObjectSet objectSet = db.query(Item.class);
		db.store(objectSet.next());
		} finally {
			db.close();
		}
	}

	private ObjectContainer openContainer() {
		Configuration configuration = Db4o.newConfiguration();
		if(_useListTypeHandler){
			configuration.registerTypeHandler(new SingleClassTypeHandlerPredicate(ArrayList.class), new ListTypeHandler());
		}
		ObjectContainer db = Db4o.openFile(configuration, _fileName);
		return db;
	}
	
}
