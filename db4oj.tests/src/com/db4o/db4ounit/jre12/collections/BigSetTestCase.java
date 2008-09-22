/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.internal.collections.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @decaf.ignore
 * @sharpen.ignore
 */
public class BigSetTestCase extends AbstractDb4oTestCase implements OptOutCS{
	
	private static final Item ITEM_ONE = new Item("one");
	
	private static final Item[] items = new Item[]{
		new Item("one"),
		new Item("two"),
		new Item("three"),
	};

	public static class Holder <E> {
		
		public Set<E> _set;
		
	}
	
	public static class Item {
		public String _name;
		
		public Item(String name){
			_name = name;
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Item)){
				return false;
			}
			Item other = (Item) obj;
			if(_name == null){
				return other._name == null;
			}
			return _name.equals(other._name);
		}
	}
	
	public void testTypeHandlerInstalled(){
		TypeHandler4 typeHandler = container().handlers().configuredTypeHandler(reflector().forClass(BigSet.class));
		Assert.isInstanceOf(BigSetTypeHandler.class, typeHandler);
	}
	
	public void testEmptySet(){
		BigSet set = new BigSet(db());
		Assert.areEqual(0, set.size()); 
	}
	
	public void testAddSize(){
		BigSet set = new BigSet(db());
		set.add(ITEM_ONE);
		Assert.areEqual(1, set.size());
	}
	
	public void testContains(){
		BigSet set = new BigSet(db());
		set.add(ITEM_ONE);
		Assert.isTrue(set.contains(ITEM_ONE));
	}
	
	public void testPersistence() throws Exception{
		Holder holder = new Holder();
		holder._set = new BigSet(db());
		Set set = holder._set;
		set.add(ITEM_ONE);
		store(holder);
		reopen();
		holder = (Holder) retrieveOnlyInstance(Holder.class);
		Item expectedItem = (Item)retrieveOnlyInstance(Item.class);
		set = holder._set;
		Assert.isNotNull(set);
		Assert.areEqual(1, set.size());
		Iterator setIterator = holder._set.iterator();
		Assert.isNotNull(setIterator);
		Assert.isTrue(setIterator.hasNext());
		Item actualItem = (Item) setIterator.next();
		Assert.areSame(expectedItem, actualItem);
	}
	
	public void testAddAllContainsAll(){
		BigSet set = new BigSet(db());
		Collection collection = itemCollection();
		set.addAll(collection);
		Assert.isTrue(set.containsAll(collection));
	}
	
	public void testRemove(){
		BigSet set = new BigSet(db());
		Collection collection = itemCollection();
		set.addAll(collection);
		Object first = collection.iterator().next();
		set.remove(first);
		Assert.isTrue(collection.remove(first));
		Assert.isFalse(collection.remove(first));
		Assert.isTrue(set.containsAll(collection));
		Assert.isFalse(set.contains(first));
	}
	
	public void testRemoveAll(){
		BigSet set = new BigSet(db());
		Collection collection = itemCollection();
		set.addAll(collection);
		Assert.isTrue(set.removeAll(collection));
		Assert.areEqual(0, set.size());
		Assert.isFalse(set.removeAll(collection));
	}
	
	public void testIsEmpty(){
		BigSet set = new BigSet(db());
		Assert.isTrue(set.isEmpty());
		set.add(ITEM_ONE);
		Assert.isFalse(set.isEmpty());
		set.remove(ITEM_ONE);
		Assert.isTrue(set.isEmpty());
	}
	
	public void testIterator(){
		BigSet set = new BigSet(db());
		Collection collection = itemCollection();
		set.addAll(collection);
		
		Iterator i = set.iterator();
		Assert.isNotNull(i);
		IteratorAssert.sameContent(collection.iterator(), i);
	}

	private Collection itemCollection() {
		Collection c = new ArrayList<Item>();
		for (int i = 0; i < items.length; i++) {
			c.add(items[i]);
		}
		return c;
	}
	
	
	

}
