/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.AbstractClientServerDb4oFixture;

public class GlobalLifecycleEventsTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] arguments) {
		new GlobalLifecycleEventsTestCase().runClientServer();
	}
	
	public static final class Item {

		public int id;

		public Item(int id_) {
			id = id_;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof Item)) return false;
			return ((Item)obj).id == id;
		} 
		
		public String toString() {
			return "Item(" + id + ")";
		}
	}
	
	private EventRecorder _recorder;
	
	protected void db4oSetupBeforeStore() throws Exception {
		_recorder = new EventRecorder();
	}	
	
	public void testActivating() throws Exception {
		storeAndReopen();
		assertActivationEvent(eventRegistry().activating());
	}
	
	public void testCancelDeactivating() {
		listenToEvent(eventRegistry().deactivating());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		db().deactivate(item, 1);
		
		assertSingleObjectEventArgs(eventRegistry().deactivating(), item);
		
		Assert.areEqual(1, item.id);
	}
	
	public void testDeleting() throws Exception {
		assertDeletionEvent(eventRegistryForDelete().deleting());
	}
	
	public void testDeleted() throws Exception {
		assertDeletionEvent(eventRegistryForDelete().deleted());
	}
	
	private void assertDeletionEvent(Event4 event4) throws Exception {
		assertDeletionEvent(event4, false);
	}

	private void assertDeletionEvent(Event4 event, boolean cancel) throws Exception {
		listenToEvent(event);
		
		Item item = storeItem();
		if (cancel) {
			_recorder.cancel(true);
		}
		
		Item expectedItem = isClientServer() ? queryServerItem(item) : item;
		
		if (isClientServer()) {
			// server needs some time to dispatch the events asynchronously
			// let's wait some time on the _recorder
			synchronized (_recorder) {
				db().delete(item);
				_recorder.wait(100);
			}
		} else {
			db().delete(item);
		}
		
		assertSingleObjectEventArgs(event, expectedItem);
		
		if (cancel) {
			Assert.areSame(item, db().get(Item.class).next());
		} else {
			Assert.areEqual(0, db().get(Item.class).size());
		}
	}
	
	public void testCancelDeleting() throws Exception {
		assertDeletionEvent(eventRegistryForDelete().deleting(), true);
	}	
	
	public void testCancelCreating() {	
		listenToEvent(eventRegistry().creating());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		
		assertSingleObjectEventArgs(eventRegistry().creating(), item);
		
		Assert.areEqual(0, db().get(Item.class).size());
	}

	public void testCancelUpdating() throws Exception {
		listenToEvent(eventRegistry().updating());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		item.id = 42;
		db().set(item);
		
		assertSingleObjectEventArgs(eventRegistry().updating(), item);
		
		reopen();
		
		item = (Item)db().get(Item.class).next();
		Assert.areEqual(1, item.id);
	}
	
	public void testCreating() {
		assertCreationEvent(eventRegistry().creating());
	}
	
	public void testDeactivating() throws Exception {
		assertDeactivationEvent(eventRegistry().deactivating());
	}
	
	public void testUpdating() {
		assertUpdateEvent(eventRegistry().updating());
	}
	
	public void testActivated() throws Exception {
		storeAndReopen();
		assertActivationEvent(eventRegistry().activated());
	}

	public void testDeactivated() throws Exception {
		assertDeactivationEvent(eventRegistry().deactivated());
	}
	
	public void testCreated() {
		assertCreationEvent(eventRegistry().created());
	}
	
	public void testUpdated() {
		assertUpdateEvent(eventRegistry().updated());
	}

	private void assertActivationEvent(Event4 event) throws Exception {
		listenToEvent(event);
		
		Item item = (Item)db().get(Item.class).next();
		
		assertSingleObjectEventArgs(event, item);
	}
	
	private void assertCreationEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areSame(item, db().get(Item.class).next());
	}
	
	private void assertDeactivationEvent(Event4 event) throws Exception {
		listenToEvent(event);
		
		Item item = storeItem();
		db().deactivate(item, 1);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areEqual(0, item.id);
	}
	
	private Item queryServerItem(Item item) {
		return (Item)fileSession().get(item).next();
	}

	private void assertSingleObjectEventArgs(Event4 expectedEvent, Item expectedItem) {
		Assert.areEqual(1, _recorder.size());
		EventRecord record = _recorder.get(0);
		Assert.areSame(expectedEvent, record.e);
		
		final Object actual = ((ObjectEventArgs)record.args).object();
		Assert.areSame(expectedItem, actual);
	}

	private boolean isClientServer() {
		return fixture() instanceof AbstractClientServerDb4oFixture;
	}
	
	private void assertUpdateEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();		
		item.id = 42;
		db().set(item);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areSame(item, db().get(Item.class).next());
	}
	
	private EventRegistry eventRegistryForDelete() {
		return EventRegistryFactory.forObjectContainer(fileSession());
	}

	private EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
	
	private void listenToEvent(Event4 event) {
		event.addListener(_recorder);
	}
	
	private void storeAndReopen() throws Exception {
		storeItem();
		reopen();
	}

	private Item storeItem() {
		Item item = new Item(1);
		db().set(item);
		db().commit();
		return item;
	}
	
}
