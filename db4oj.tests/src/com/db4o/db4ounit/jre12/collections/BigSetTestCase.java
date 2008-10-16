/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.collections.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @decaf.ignore.jdk11
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
		TypeHandler4 typeHandler = container().handlers().configuredTypeHandler(reflector().forClass(newBigSet().getClass()));
		Assert.isInstanceOf(BigSetTypeHandler.class, typeHandler);
	}
	
	public void testEmptySet(){
		Set<Item> set = newBigSet();
		Assert.areEqual(0, set.size()); 
	}

	/**
	 * @sharpen.ignore
	 */
	public void testAdd(){
		Set<Item> set = newBigSet();
		Assert.isTrue(set.add(ITEM_ONE));
		Assert.isFalse(set.add(ITEM_ONE));
		Assert.areEqual(1, set.size());
	}
	
	public void testSize(){
		Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		Assert.areEqual(1, set.size());
		set.remove(ITEM_ONE);
		Assert.areEqual(0, set.size());
		Item itemTwo = new Item("two");
		set.add(itemTwo);
		set.add(new Item("three"));
		Assert.areEqual(2, set.size());
		set.remove(itemTwo);
		Assert.areEqual(1, set.size());
	}
	
	public void testContains(){
		Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		Assert.isTrue(set.contains(ITEM_ONE));
	}
	
	public void testPersistence() throws Exception{
		Holder<Item> holder = new Holder<Item>();
		holder._set = newBigSet();
		Set<Item> set = holder._set;
		set.add(ITEM_ONE);
		store(holder);
		reopen();
		holder = (Holder<Item>) retrieveOnlyInstance(holder.getClass());
		set = holder._set;
		assertSinglePersistentItem(set);
	}

	private void assertSinglePersistentItem(Set<Item> set) {
		Item expectedItem = (Item)retrieveOnlyInstance(Item.class);
		Assert.isNotNull(set);
		Assert.areEqual(1, set.size());
		Iterator setIterator = set.iterator();
		Assert.isNotNull(setIterator);
		Assert.isTrue(setIterator.hasNext());
		Item actualItem = (Item) setIterator.next();
		Assert.areSame(expectedItem, actualItem);
	}
	
	public void testAddAllContainsAll(){
		final Set<Item> set = newBigSet();
		final List<Item> collection = itemList();
		Assert.isTrue(set.addAll(collection));
		Assert.isTrue(set.containsAll(collection));
		
		Assert.isFalse(set.addAll(collection));
		Assert.areEqual(collection.size(), set.size());
	}
	
	public void testRemove(){
		Set<Item> set = newBigSet();
		List<Item> collection = itemList();
		set.addAll(collection);
		Item first = collection.get(0);
		set.remove(first);
		Assert.isTrue(collection.remove(first));
		Assert.isFalse(collection.remove(first));
		Assert.isTrue(set.containsAll(collection));
		Assert.isFalse(set.contains(first));
	}
	
	public void testRemoveAll(){
		Set<Item> set = newBigSet();
		List<Item> collection = itemList();
		set.addAll(collection);
		Assert.isTrue(set.removeAll(collection));
		Assert.areEqual(0, set.size());
		Assert.isFalse(set.removeAll(collection));
	}
	
	public void testIsEmpty(){
		Set<Item> set = newBigSet();
		Assert.isTrue(set.isEmpty());
		set.add(ITEM_ONE);
		Assert.isFalse(set.isEmpty());
		set.remove(ITEM_ONE);
		Assert.isTrue(set.isEmpty());
	}
	
	public void testIterator(){
		Set<Item> set = newBigSet();
		Collection<Item> collection = itemList();
		set.addAll(collection);
		
		Iterator i = set.iterator();
		Assert.isNotNull(i);
		IteratorAssert.sameContent(collection.iterator(), i);
	}
	
	public void testDelete() throws Throwable{
		final Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		db().store(set);
		db().commit();
		BTree bTree = bTree(set);
		BTreeAssert.assertAllSlotsFreed(fileTransaction(), bTree, new CodeBlock() {
			public void run() throws Throwable {
				db().delete(set);
				db().commit();
			}
		});
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				set.add(ITEM_ONE);
			}
		});
	}
	
	public void testDefragment() throws Exception{
		Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		db().store(set);
		db().commit();
		defragment();
		set = (Set<Item>) retrieveOnlyInstance(set.getClass());
		assertSinglePersistentItem(set);
	}
	
	public void testClear(){
		Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		set.clear();
		Assert.areEqual(0, set.size());
	}

	private List<Item> itemList() {
		List<Item> c = new ArrayList<Item>();
		for (int i = 0; i < items.length; i++) {
			c.add(items[i]);
		}
		return c;
	}
	
	public void testGetInternalImplementation() throws Exception{
		Set<Item> set = newBigSet();
		BTree bTree = bTree(set);
		Assert.isNotNull(bTree);
	}
	
	private Set<Item> newBigSet() {
		return CollectionFactory.forObjectContainer(db()).<Item>newBigSet();
	}

	public static BTree bTree(Set<Item> set) throws IllegalAccessException{
		return (BTree)Reflection4.getFieldValue(set, "_bTree");
	}
	
	private LocalTransaction fileTransaction() {
		return ((LocalTransaction)trans());
	}
	
}
