/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.util.*;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class UpdateDepthTestCase extends AbstractDb4oTestCase {
	
	public static final class Item {
		
		public String name;
		public Item child;
		public Item[] childArray;
		public Vector childVector;
		
		public Item() {
		}
		
		public Item(String name) {
			this.name = name;
		}
		
		public Item(String name, Item child) {
			this(name);
			this.child = child;
		}
		
		public Item(String name, Item child, Item[] childArray) {
			this(name, child);
			this.childArray = childArray;
			this.childVector = new Vector();
			for (int i=0; i<childArray.length; ++i) {
				childVector.addElement(childArray[i]);
			}
		}
	}
	
	public static final class RootItem {
		public Item root;
		
		public RootItem() {
		}
		
		public RootItem(Item root) {
			this.root = root;
		}
	}
	
	protected void store() throws Exception {
		store(new RootItem(newGraph()));
		
	}
	
	protected void configure(Configuration config) throws Exception {
		final ObjectClass itemClass = config.objectClass(Item.class);
		itemClass.updateDepth(1);
		itemClass.minimumActivationDepth(3);
	}	
	
	public void testDepth0() throws Exception {
		
		db().set(pokeName(queryRoot()), 0);
		
		expect(newGraph());
	}
	
	public void testDepth1() throws Exception {
		
		final Item item = pokeChild(pokeName(queryRoot()));
		
		db().set(item, 1);
		
		expect(pokeName(newGraph()));
	}
	
	public void testDepth2() throws Exception {
		
		final Item root = pokeChild(pokeName(queryRoot()));
		pokeChild(root.child); // one level too many
		
		db().set(root, 2);
		
		expect(pokeChild(pokeName(newGraph())));
	}
	
	public void testDepth3() throws Exception {
		final Item item = pokeChild(pokeName(queryRoot()));
		pokeChild(item.child);
		
		db().set(item, 3);
		
		expect(item);
	}
	
	private Item newGraph() {
		return new Item("Level 1",
			new Item("Level 2",
				new Item("Level 3"),
				new Item[] { new Item("Array Level 3") }),
			new Item[] { new Item("Array Level 2") });
	}

	private Item pokeChild(final Item item) {
		pokeName(item.child);
		if (item.childArray != null) {
			pokeName(item.childArray[0]);
			pokeName((Item) item.childVector.elementAt(0));
		}
		return item;
	}
	
	private Item pokeName(Item item) {
		item.name = item.name + "*";
		return item;
	}

	private void expect(Item expected) throws Exception {
		reopen();
		assertEquals(expected, queryRoot());
	}

	private void assertEquals(Item expected, Item actual) {
		if (expected == null) {
			Assert.isNull(actual);
			return;
		}
		Assert.isNotNull(actual);
		Assert.areEqual(expected.name, actual.name);
		assertEquals(expected.child, actual.child);
		assertEquals(expected.childArray, actual.childArray);
		assertCollection(expected.childVector, actual.childVector);
	}

	private void assertCollection(Vector expected, Vector actual) {
		if (expected == null) {
			Assert.isNull(actual);
			return;
		}
		Assert.isNotNull(actual);
		Assert.areEqual(expected.size(), actual.size());
		for (int i=0; i<expected.size(); ++i) {
			assertEquals((Item)expected.elementAt(i), (Item)actual.elementAt(i));
		}
	}

	private void assertEquals(Item[] expected, Item[] actual) {
		if (expected == null) {
			Assert.isNull(actual);
			return;
		}
		Assert.isNotNull(actual);
		Assert.areEqual(expected.length, actual.length);
		for (int i=0; i<expected.length; ++i) {
			assertEquals(expected[i], actual[i]);
		}
	}
	
	private Item queryRoot() {
		return ((RootItem)newQuery(RootItem.class).execute().next()).root;
	}

}
