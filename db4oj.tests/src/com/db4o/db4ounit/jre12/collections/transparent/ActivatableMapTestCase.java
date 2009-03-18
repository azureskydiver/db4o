/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.db4ounit.jre12.collections.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableMapTestCase extends AbstractDb4oTestCase {

	public static class Item<K,V> {
		public Map<K,V> _map;
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}
	
	@Override
	protected void store() throws Exception {
		Item<CollectionElement, CollectionElement> item = new Item();
		item._map = createMap();
		store(item);
	}

	public void testCreation() {
		new ActivatableHashMap<CollectionElement, CollectionElement>();
		new ActivatableHashMap<CollectionElement, CollectionElement>(42);
		new ActivatableHashMap<CollectionElement, CollectionElement>(42, (float)0.5);
		HashMap<CollectionElement,CollectionElement> origMap = new HashMap<CollectionElement,CollectionElement>();
		origMap.put(new Element("a"), new Element("b"));
		ActivatableHashMap<CollectionElement, CollectionElement> fromMap = 
			new ActivatableHashMap<CollectionElement, CollectionElement>(origMap);
		assertEqualContent(origMap, fromMap);
	}
	
	public void testCorrectContent() {
		assertEqualContent(createMap(), singleMap());
	}

	public void testActivatableElementsAreNotActivated() {
		long[] ids = ActivatableCollectionTestUtil.allActivatableElementIds(db());
		for (long id : ids) {
			Assert.isFalse(db().isActive(db().getByID(id)));
		}
	}

	public void testClear() throws Exception {
		singleMap().clear();
		reopen();
		assertEqualContent(new HashMap<CollectionElement,CollectionElement>(), singleMap());
	}
	
	private void assertEqualContent(
			Map<CollectionElement, CollectionElement> expected,
			Map<CollectionElement, CollectionElement> actual) {
		IteratorAssert.areEqual(expected.keySet().iterator(), actual.keySet().iterator());
		for (CollectionElement key : actual.keySet()) {
			Assert.areEqual(expected.get(key), actual.get(key));
		}
	}
	
	public void testMapIsNotActivated(){
		Assert.isFalse(db().isActive(singleMap()));
	}
	
	private Map<CollectionElement,CollectionElement> createMap() {
		ActivatableHashMap<CollectionElement, CollectionElement> map = new ActivatableHashMap<CollectionElement, CollectionElement>();
		map.put(new Element("plain/plain key"), new Element("plain/plain value"));
		map.put(new Element("plain/activatable key"), new ActivatableElement("plain/activatable value"));
		map.put(new ActivatableElement("activatable/plain key"), new Element("activatable/plain value"));
		map.put(new ActivatableElement("activatable/activatable key"), new ActivatableElement("activatable/activatable value"));
		return map;
	}
	
	private Item singleItem() {
		return retrieveOnlyInstance(Item.class);
	}
	
	private Map<CollectionElement,CollectionElement> singleMap(){
		return singleItem()._map;
	}
}
