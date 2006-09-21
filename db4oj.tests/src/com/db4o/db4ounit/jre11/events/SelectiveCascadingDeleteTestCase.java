package com.db4o.db4ounit.jre11.events;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.events.*;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.Db4oSolo;

public class SelectiveCascadingDeleteTestCase extends AbstractDb4oTestCase {
	
	protected void configure() {
		enableCascadeOnDelete();
	}
	
	protected void store() {
		Item c = new Item("C", null);
		Item b = new Item("B", c);
		Item a = new Item("A", b);
		db().set(a);
	}
	
	public void testPreventMiddleObjectDeletion() throws Exception {
		Assert.areEqual(3, queryItems().size());
		
		eventRegistry().deleting().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				CancellableObjectEventArgs a = (CancellableObjectEventArgs)args;
				Item item = ((Item)a.object());
                if (item.id.equals("B")) {
					// cancel deletion of this item
					a.cancel();
					
					// restart from the child
                    SelectiveCascadingDeleteTestCase.this.db().delete(item.child);
					
					// and disconnect it
					item.child = null;
                    SelectiveCascadingDeleteTestCase.this.db().set(item);
				}
			}
		});

        Item a = queryItem("A");
		Assert.isNotNull(a);		
		db().delete(a);
		
		reopen();
		
		ObjectSet found = queryItems();
		Assert.areEqual(1, found.size());
		final Item remainingItem = ((Item)found.next());
		Assert.areEqual("B", remainingItem.id);
		Assert.isNull(remainingItem.child);
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
			private static final long serialVersionUID = 1L;
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
