/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @exclude
 */
public class CommitTimeCallbacksTestCase extends AbstractDb4oTestCase {

	private static final Item[] NONE = new Item[0];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CommitTimeCallbacksTestCase().runSolo();
	}

	
	public static final class Item {
		public int id;
		
		public Item() {
		}
		
		public Item(int id_) {
			id = id_;
		}
		
		public String toString() {
			return "Item(" + id + ")";
		}
	}

	private EventRecorder _eventRecorder;	
	
	protected void store() throws Exception {
		for (int i=0; i<3; ++i) {
			store(new Item(i));
		}
	}
	
	protected void db4oSetupAfterStore() throws Exception {
		_eventRecorder = new EventRecorder();
		committing().addListener(_eventRecorder);
	}
	
	public void testCommittingAdded() {
		
		final Item item4 = new Item(4);
		final Item item5 = new Item(5);
		db().set(item4);
		db().set(item5);
		
		assertNoEvents();
		
		db().commit();
		
		assertCommittingEvent(new Item[] { item4, item5 }, NONE, NONE);
	}
	
	public void _testCommittingAddedDeleted() {
		
		final Item item4 = new Item(4);
		final Item item1 = getItem(1);
		final Item item2 = getItem(2);
		db().set(item4);
		db().delete(item1);
		db().delete(item2);
		
		db().commit();
		assertCommittingEvent(new Item[] { item4 }, new Item[] { item1, item2 }, NONE);
	}

	private Item getItem(int id) {
		final Query query = newQuery(Item.class);
		query.descend("id").constrain(new Integer(id));
		return (Item)query.execute().next();
	}

	private void assertCommittingEvent(
			final Item[] expectedAdded,
			final Item[] expectedDeleted,
			final Item[] expectedUpdated) {
		
		Assert.areEqual(1, _eventRecorder.size());
		Assert.areSame(committing(), _eventRecorder.get(0).e);
		CommitEventArgs args = (CommitEventArgs)_eventRecorder.get(0).args;
		assertContainsAll(expectedAdded, args.added());
		assertContainsAll(expectedDeleted, args.deleted());
		assertContainsAll(expectedUpdated, args.updated());
	}

	private void assertNoEvents() {
		Assert.areEqual(0, _eventRecorder.size());
	}

	private Event4 committing() {
		return eventRegistry().committing();
	}

	private EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
	
	private void assertContainsAll(Item[] expectedItems, ObjectInfoCollection actualItems) {
		for (int i = 0; i < expectedItems.length; i++) {
			assertContains(expectedItems[i], actualItems);
		}
		Assert.areEqual(expectedItems.length, Iterators.size(actualItems));
	}

	private void assertContains(Item expectedItem, ObjectInfoCollection items) {
		final Iterator4 iterator = items.iterator();
		while (iterator.moveNext()) {
			ObjectInfo info = (ObjectInfo)iterator.current();
			if (expectedItem == info.getObject()) {
				return;
			}
		}
		Assert.fail("Object '" + expectedItem + "' not found.");
	}

}
