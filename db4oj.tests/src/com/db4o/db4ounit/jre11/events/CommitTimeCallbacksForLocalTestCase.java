/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;
import com.db4o.query.Query;

import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.OptOutCS;

/**
 * @exclude
 */
public class CommitTimeCallbacksForLocalTestCase extends AbstractDb4oTestCase implements OptOutCS {

	private static final Item[] NONE = new Item[0];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CommitTimeCallbacksForLocalTestCase().runSolo();
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
	
	public void testCommittingAddedDeleted() {
		
		final Item item4 = new Item(4);
		final Item item1 = getItem(1);
		final Item item2 = getItem(2);
		db().set(item4);
		db().delete(item1);
		db().delete(item2);
		
		db().commit();
		assertCommittingEvent(new Item[] { item4 }, new Item[] { item1, item2 }, NONE);
	}
	
	public void testCommittingAddedUpdatedDeleted() {
		final Item item1 = getItem(1);
		final Item item2 = getItem(2);
		final Item item4 = new Item(4);
		db().set(item4);
		db().set(item2);
		db().delete(item1);
		
		db().commit();
		assertCommittingEvent(new Item[] { item4 }, new Item[] { item1 }, new Item[] { item2 });
	}
	
	public void testObjectSetTwiceShouldStillAppearAsAdded() {
		final Item item4 = new Item(4);
		db().set(item4);
		db().set(item4);
		db().commit();
		assertCommittingEvent(new Item[] { item4 }, NONE, NONE);
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
		
		EventAssert.assertCommitEvent(_eventRecorder, committing(), expectedAdded, expectedDeleted, expectedUpdated);
	}

	private void assertNoEvents() {
		EventAssert.assertNoEvents(_eventRecorder);
	}

	private Event4 committing() {
		return eventRegistry().committing();
	}

	private EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
}
