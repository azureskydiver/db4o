/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit;

import com.db4o.events.*;

import db4ounit.Assert;
import db4ounit.db4o.Db4oTestCase;

public class GlobalLifecycleEventsTestCase extends Db4oTestCase {
	
	private EventRecorder _recorder;
	
	public void setUp() throws Exception {
		super.setUp();
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
	
	public void testCancelDeleting() {
		listenToEvent(eventRegistry().deleting());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		db().delete(item);
		
		assertSingleObjectEventArgs(eventRegistry().deleting(), item);
		
		Assert.areSame(item, db().get(Item.class).next());
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
	
	public void testDeleting() {
		assertDeletionEvent(eventRegistry().deleting());
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
	
	public void testDeleted() {
		assertDeletionEvent(eventRegistry().deleted());
	}
	
	public void testObjectOnNew() {
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
	
	private void assertDeletionEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();
		db().delete(item);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areEqual(0, db().get(Item.class).size());
	}
	
	private void assertSingleObjectEventArgs(Event4 expectedEvent, Item expectedItem) {
		Assert.areEqual(1, _recorder.size());
		EventRecord record = _recorder.get(0);
		Assert.areSame(expectedEvent, record.e);
		Assert.areSame(expectedItem, ((ObjectEventArgs)record.args).subject());
	}
	
	private void assertUpdateEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();		
		item.id = 42;
		db().set(item);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areSame(item, db().get(Item.class).next());
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
		return item;
	}
}
