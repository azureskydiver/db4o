package com.db4o.db4ounit.events;

import com.db4o.ObjectSet;
import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

import db4ounit.Assert;
import db4ounit.db4o.Db4oTestCase;

public class SelectiveCascadingDeleteTestCase extends Db4oTestCase {
	
	public static class Item {
		public String id;
		public Item child;
		
		public Item() {
		}
		
		public Item(String id, Item child) {
			this.id = id;
			this.child = child;
		}
	}
	
	protected void store() {
		Item c = new Item("C", null);
		Item b = new Item("B", c);
		Item a = new Item("A", b);
		db().set(a);
	}
	
	public void testPreventMiddleObjectDeletion() {
		Assert.areEqual(3, queryItems().size());
		
		enableCascadeOnDelete();
		
		eventRegistry().deleting().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				CancellableObjectEventArgs a = (CancellableObjectEventArgs)args;
				if (((Item)a.object()).id.equals("B")) {
					a.cancel();
				}
			}
		});
		
		Item a = queryItem("A");
		Assert.isNotNull(a);		
		db().delete(a);
		
		ObjectSet found = queryItems();
		Assert.areEqual(2, found.size());
		Assert.areEqual("B", ((Item)found.next()).id);
		Assert.areEqual("C", ((Item)found.next()).id);
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
		db().configure().objectClass(Item.class).cascadeOnDelete(true);
	}

	private Item queryItem(final String id) {
		Query q = createItemQuery();
		q.descend("id").constrain(id);
		return (Item)q.execute().next();
	}
}
