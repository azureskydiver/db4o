/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.config.Configuration;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.Query;

import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.OptOutCS;

/**
 * @exclude
 */
public class CommitTimeCallbacksForLocalTestCase extends AbstractDb4oTestCase implements OptOutCS {

	private static final ObjectInfo[] NONE = new ObjectInfo[0];

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
	
	protected void configure(Configuration config) {
		indexField(config, Item.class, "id");
	}
	
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
		
		Item item4 = new Item(4);
		Item item5 = new Item(5);
		db().set(item4);
		db().set(item5);
		
		ObjectInfo info4 = getInfo(4);
		ObjectInfo info5 = getInfo(5);
		
		assertNoEvents();
		
		db().commit();
		
		assertCommittingEvent(new ObjectInfo[] { info4, info5 }, NONE, NONE);
	}
	
	public void testCommittingAddedDeleted() {
		
		Item item4 = new Item(4);
		Item item1 = getItem(1);
		Item item2 = getItem(2);
		
		ObjectInfo info1 = getInfo(1);
		ObjectInfo info2 = getInfo(2);
		
		db().set(item4);
		db().delete(item1);
		db().delete(item2);
		
		ObjectInfo info4 = getInfo(4);
		
		db().commit();
		assertCommittingEvent(new ObjectInfo[] { info4 }, new ObjectInfo[] { info1, info2 }, NONE);
	}
	
	public void testCommittingAddedUpdatedDeleted() {
		Item item1 = getItem(1);
		Item item2 = getItem(2);
		
		ObjectInfo info1 = getInfo(1);
		ObjectInfo info2 = getInfo(2);
		
		Item item4 = new Item(4);
		db().set(item4);
		db().set(item2);
		db().delete(item1);
		
		ObjectInfo info4 = getInfo(4);
		
		db().commit();
		assertCommittingEvent(new ObjectInfo[] { info4 }, new ObjectInfo[] { info1 }, new ObjectInfo[] { info2 });
	}
	
	public void testObjectSetTwiceShouldStillAppearAsAdded() {
		final Item item4 = new Item(4);
		db().set(item4);
		db().set(item4);
		
		ObjectInfo info4 = getInfo(4);
		
		db().commit();
		assertCommittingEvent(new ObjectInfo[] { info4 }, NONE, NONE);
	}
	
	private Item getItem(int id) {
		final Query query = newQuery(Item.class);
		query.descend("id").constrain(new Integer(id));
		return (Item)query.execute().next();
	}
	
	private ObjectInfo getInfo(int itemId) {
		Item item = getItem(itemId);
		int internalId = (int) db().getID(item);
		return new LazyObjectReference((ObjectContainerBase) db(), internalId );
	}
	

	private void assertCommittingEvent(
			final ObjectInfo[] expectedAdded,
			final ObjectInfo[] expectedDeleted,
			final ObjectInfo[] expectedUpdated) {
		
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
