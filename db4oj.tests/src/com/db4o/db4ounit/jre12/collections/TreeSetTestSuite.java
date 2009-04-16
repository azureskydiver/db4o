/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * @exclude
 */
@decaf.Remove(decaf.Platform.JDK11)
public class TreeSetTestSuite extends FixtureTestSuiteDescription implements Db4oTestCase {
	
	static final Comparator<String> comparator = new Comparator<String>() {
		public int compare(String x, String y) {
			return y.compareTo(x);
        }
	};
	
	{
		fixtureProviders(
			new Db4oFixtureProvider(),
			new SubjectFixtureProvider(
				new Deferred4<TreeSet>() { public TreeSet value() {
					
					return new TreeSet();
					
				}},
				new Deferred4<TreeSet>() { public TreeSet value() {
					
					return new TreeSet(comparator);
					
				}}
			)
		);
		
		testUnits(TestUnit.class);
	}
	
	public static class TestUnit extends AbstractDb4oTestCase {

		private static final String[] ELEMENTS = new String[] { "one", "two" };

		public static class Item {

			public Item(TreeSet treeSet) {
				this.treeSet = treeSet;
			}

			public TreeSet treeSet;

		}

		@Override
		protected void store() {
			final TreeSet treeSet = subject();
			treeSet.addAll(Arrays.asList(ELEMENTS));
			
			store(new Item(treeSet));
		}

		private TreeSet subject() {
	        return SubjectFixtureProvider.value();
        }
		
		public void testIdentity() throws Exception {
			final TreeSet existingTreeSet = retrieveOnlyInstance(Item.class).treeSet;
			store(new Item(existingTreeSet));
			reopen();
			
			final ObjectSet<Item> items = db().query(Item.class);
			Assert.areEqual(2, items.size());
			assertTreeSetContent(items.get(0).treeSet);
			Assert.areSame(items.get(0).treeSet, items.get(1).treeSet);
		}

		public void testRoundtrip() {
			final Item item = retrieveOnlyInstance(Item.class);
			assertTreeSetContent(item.treeSet);
		}

		private void assertTreeSetContent(final TreeSet treeSet) {
	        final Comparator comparator = subject().comparator();
			final Object[] expected = comparator == null
				? ELEMENTS
				: sortedBy(ELEMENTS, comparator);
			IteratorAssert.areEqual(expected, treeSet.iterator());
        }
		
		public void testTransparentDescendOnElement() {
			
			final Item item = retrieveOnlyInstance(Item.class);
			store(new Item(null));

			for (String element : ELEMENTS) {
				final ObjectSet<Object> found = itemByTreeSetElement(element);
				Assert.areSame(item, found.next());
			}
			
			final Item copy = new Item(new TreeSet(item.treeSet));
			store(copy);
			
			for (String element : ELEMENTS) {
				final ObjectSet<Object> found = itemByTreeSetElement(element);
				ObjectSetAssert.sameContent(found, item, copy);
			}
		}
		
		static class NamedItem {

			public String name;

			public NamedItem(String name) {
				this.name = name;
			}
			
		}
		
		public void testTransparentDescendOnElementMember() {
			
			deleteAll(Item.class);
			
			final Item item1 = new Item(new TreeSet(Arrays.asList(
										new NamedItem("foo"))));
			final Item item2 = new Item(new TreeSet(Arrays.asList(
										new NamedItem("bar"))));
			
			final Item[] items = { item1, item2 };
			for (Item item : items)
				store(item);
			
			for (Item item : items) {
				final NamedItem firstNamedItem = (NamedItem)item.treeSet.first();
				final ObjectSet<Object> found = itemByNamedItem(firstNamedItem.name);
				ObjectSetAssert.sameContent(found, item);
			}
		}

		private ObjectSet<Object> itemByNamedItem(final String namedItemName) {
			final Query query = newQuery(Item.class);
			query.descend("treeSet").descend("name").constrain(namedItemName);
			final ObjectSet<Object> found = query.<Object>execute();
			return found;
		}
		
		private ObjectSet<Object> itemByTreeSetElement(String element) {
	        final Query query = newQuery(Item.class);
	        query.descend("treeSet").constrain(element);
	        return query.<Object>execute();
        }

		private String[] sortedBy(String[] elements, Comparator comparator) {
			final String[] copy = new String[elements.length];
			System.arraycopy(elements, 0, copy, 0, elements.length);
			Arrays.sort(copy, comparator);
			return copy;
        }

	}
}
