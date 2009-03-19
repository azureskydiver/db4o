/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent;

import java.util.*;

import com.db4o.activation.*;
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

	public void testMapIsNotActivated(){
		Assert.isFalse(db().isActive(singleMap()));
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
		assertEmpty();
	}

	public void testClone() throws Exception{
		ActivatableHashMap cloned = (ActivatableHashMap) ((HashMap)singleMap()).clone();
		// assert that activator is null - should throw IllegalStateException if it isn't
		cloned.bind(new Activator() {
			public void activate(ActivationPurpose purpose) {
			}
		});
		assertEqualContent(createMap(), cloned);
	}
	
	public void testContainsKey() {
		Map<CollectionElement, CollectionElement> actual = singleMap();
		for (CollectionElement expectedKey : createMap().keySet()) {
			Assert.isTrue(actual.containsKey(expectedKey));
		}
	}
	
	public void testContainsValue() {
		Map<CollectionElement, CollectionElement> actual = singleMap();
		for (CollectionElement expectedValue : createMap().values()) {
			Assert.isTrue(actual.containsValue(expectedValue));
		}
	}
	
	public void testEntrySet() {
		IteratorAssert.areEqual(createMap().entrySet().iterator(), singleMap().entrySet().iterator());
	}
	
	public void testGet() {
		assertEqualContent(createMap(), singleMap());
	}
	
	public void testIsEmpty() {
		Assert.isFalse(singleMap().isEmpty());
	}
	
	public void testKeySet() {
		IteratorAssert.areEqual(createMap().keySet().iterator(), singleMap().keySet().iterator());
	}
	
	public void testPut() throws Exception {
		Map<CollectionElement, CollectionElement> map = singleMap();
		Element value = new Element("added value");
		Element key = new Element("added key");
		map.put(key, value);
		reopen();
		Assert.areEqual(value, singleMap().get(key));
	}
	
	public void testPutAll() throws Exception {
		Map<CollectionElement, CollectionElement> map = singleMap();
		Map<CollectionElement, CollectionElement> added = new HashMap<CollectionElement, CollectionElement>();
		added.put(new Element("added key 1"), new Element("added value 1"));
		added.put(new Element("added key 2"), new Element("added value 2"));
		map.putAll(added);
		reopen();
		Map<CollectionElement, CollectionElement> expected = createMap();
		expected.putAll(added);
		assertEqualContent(expected, singleMap());
	}
	
	public void testRemove() throws Exception {
		Map<CollectionElement, CollectionElement> map = singleMap();
		for (CollectionElement key : createMap().keySet()) {
			map.remove(key);
		}
		reopen();
		assertEmpty();
	}
	
	public void testSize() {
		Assert.areEqual(createMap().size(), singleMap().size());
	}
	
	public void testValues() {
		IteratorAssert.areEqual(createMap().values().iterator(), singleMap().values().iterator());
	}
	
	public void testEquals() {
		Map<CollectionElement, CollectionElement> expected = createMap();
		Map<CollectionElement, CollectionElement> map = singleMap();
		Assert.isTrue(map.equals(expected));
	}
	
	public void testHashCode() {
		Map<CollectionElement, CollectionElement> expected = createMap();
		Map<CollectionElement, CollectionElement> map = singleMap();
		Assert.areEqual(expected.hashCode(), map.hashCode());
	}

	public void testKeySetIteratorRemove() throws Exception {
		for (Iterator iter = singleMap().keySet().iterator(); iter.hasNext();) {
			iter.next();
			iter.remove();
		}
		reopen();
		Assert.isTrue(singleMap().isEmpty());
	}
	
	public void testRepeatedPut() throws Exception {
		Element key1 = new Element("added key 1");
		Element key2 = new Element("added key 2");
		singleMap().put(key1, new Element("added value 1"));
		db().purge();
		singleMap().put(key2, new Element("added value 2"));
		reopen();
		Map<CollectionElement,CollectionElement> retrieved = singleMap();
		Assert.isTrue(retrieved.containsKey(key1));
		Assert.isTrue(retrieved.containsKey(key2));
	}
	
	private void assertEqualContent(
			Map<CollectionElement, CollectionElement> expected,
			Map<CollectionElement, CollectionElement> actual) {
		IteratorAssert.areEqual(expected.keySet().iterator(), actual.keySet().iterator());
		for (CollectionElement key : actual.keySet()) {
			Assert.areEqual(expected.get(key), actual.get(key));
		}
	}

	private void assertEmpty() {
		assertEqualContent(new HashMap<CollectionElement,CollectionElement>(), singleMap());
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
