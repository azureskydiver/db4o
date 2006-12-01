package com.db4o.db4ounit.jre11.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class SelectiveCascadingDeleteTestCase extends AbstractDb4oTestCase implements OptOutCS {
	
	protected void configure(Configuration config) {
		enableCascadeOnDelete(config);
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

		assertIndexCount(3);

		db().delete(a);
		
		db().commit();

		reopen();
		
        a = queryItem("A");
		Assert.isNull(a);		
		assertIndexCount(1);
		
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
		return EventRegistryFactory.forObjectContainer(fileSession());
	}

	private void enableCascadeOnDelete(Configuration config) {
		config.objectClass(Item.class).cascadeOnDelete(true);
	}

	private Item queryItem(final String id) {
		Query q = createItemQuery();
		q.descend("id").constrain(id);
		ObjectSet result = q.execute();
		return result.hasNext() ? (Item)result.next() : null;
	}
	
	private void assertIndexCount(int expectedCount) {
		final int[] sum={0};
		Visitor4 visitor = new Visitor4() {
			public void visit(Object obj) {
				sum[0]++;
			}
		};
		btree().traverseKeys(fileSession().getSystemTransaction(),visitor);
		Assert.areEqual(expectedCount,sum[0]);
	}

	private BTree btree() {
		return BTreeClassIndexStrategy.btree(yapClass());
	}

	private YapClass yapClass() {
		YapClass clazz = fileSession().getYapClass(reflector().forClass(Item.class));
		return clazz;
	}

	public static void main(String[] args) {
		new SelectiveCascadingDeleteTestCase().runSolo();
	}
}
