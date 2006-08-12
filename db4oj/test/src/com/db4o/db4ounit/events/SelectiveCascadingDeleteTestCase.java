package com.db4o.db4ounit.events;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.events.*;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.db4o.Db4oTestCase;
import db4ounit.db4o.Db4oTestSuiteBuilder;
import db4ounit.db4o.fixtures.Db4oSolo;

public class SelectiveCascadingDeleteTestCase extends Db4oTestCase {
	
	public static class Item {
		public String id;
		public Item child;
		
		public Item() {
		}
		
		public Item(String id_, Item child_) {
			id = id_;
			child = child_;
		}
	}
	
	protected void configure() {
		enableCascadeOnDelete();
	}
	
	protected void store() {
		Item c = new Item("C", null);
		Item b = new Item("B", c);
		Item a = new Item("A", b);
		db().set(a);
	}
	
	public void testPreventMiddleObjectDeletion() {
		Assert.areEqual(3, queryItems().size());
		
		eventRegistry().deleted().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ObjectEventArgs a = (ObjectEventArgs)args;
				Item item = ((Item)a.object());
				if (item.id.equals("B")) {
					item.child = null;
					db().set(item);
				}
			}
		});
		
		Item a = queryItem("A");
		Assert.isNotNull(a);		
		db().delete(a);
		
		ObjectSet found = queryItems();
		Assert.areEqual(1, found.size());
		Assert.areEqual("B", ((Item)found.next()).id);
	}

	private ObjectSet queryItems() {
		return createItemQuery().execute();
	}

	private Query createItemQuery() {
		Query q = db().query();
		q.constrain(Item.class);
		q.sortBy(new QueryComparator() {
			public int compare(Object first, Object second) {
				return ((Item)first).id.compareTo(((Item)second).id);
			}
		});
		return q;
	}

	private EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}

	private void enableCascadeOnDelete() {
		Db4o.configure().objectClass(Item.class).cascadeOnDelete(true);
	}

	private Item queryItem(final String id) {
		Query q = createItemQuery();
		q.descend("id").constrain(id);
		return (Item)q.execute().next();
	}
	
	public static void main(String[] args) {
		new TestRunner(
				new Db4oTestSuiteBuilder(
						new Db4oSolo(),
						SelectiveCascadingDeleteTestCase.class)).run();
	}
}
