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

	private void listenToEvent(Event4 event) {
		event.addListener(_recorder);
	}
	
	private Item storeItem() {
		Item item = new Item(1);
		db().set(item);
		return item;
	}

	private void storeAndReopen() throws Exception {
		storeItem();
		reopen();
	}

	private void assertNewEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areSame(item, db().get(Item.class).next());
	}
	
	public void testObjectCanNew() {
		assertNewEvent(eventRegistry().objectCanNew());
	}
	
	public void testObjectOnNew() {
		assertNewEvent(eventRegistry().objectOnNew());
	}
	
	public void testObjectCanActivate() throws Exception {
		storeAndReopen();
		assertActivationEvent(eventRegistry().objectCanActivate());
	}

	private void assertActivationEvent(Event4 event) throws Exception {
		listenToEvent(event);
		
		Item item = (Item)db().get(Item.class).next();
		
		assertSingleObjectEventArgs(event, item);
	}
	
	public void testObjectOnActivate() throws Exception {
		storeAndReopen();
		assertActivationEvent(eventRegistry().objectOnActivate());
	}

	private void reopen() throws Exception {
		fixture().close();
		fixture().open();
	}
	
	public void testCancellableObjectCanNew() {	
		listenToEvent(eventRegistry().objectCanNew());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		
		assertSingleObjectEventArgs(eventRegistry().objectCanNew(), item);
		
		Assert.areEqual(0, db().get(Item.class).size());
	}
	
	private void assertUpdateEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();
		
		item.id = 42;
		db().set(item);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areSame(item, db().get(Item.class).next());
	}
	
	public void testObjectCanUpdate() {
		assertUpdateEvent(eventRegistry().objectCanUpdate());
	}
	
	public void testObjectOnUpdate() {
		assertUpdateEvent(eventRegistry().objectOnUpdate());
	}
	
	public void testCancellableObjectCanUpdate() throws Exception {
		listenToEvent(eventRegistry().objectCanUpdate());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		item.id = 42;
		db().set(item);
		
		assertSingleObjectEventArgs(eventRegistry().objectCanUpdate(), item);
		
		reopen();
		
		item = (Item)db().get(Item.class).next();
		Assert.areEqual(1, item.id);
	}

	private void assertDeleteEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();
		db().delete(item);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areEqual(0, db().get(Item.class).size());
	}
	
	public void testObjectCanDelete() {
		assertDeleteEvent(eventRegistry().objectCanDelete());
	}
	
	public void testObjectOnDelete() {
		assertDeleteEvent(eventRegistry().objectOnDelete());
	}
	
	public void testCancellableObjectCanDelete() {
		listenToEvent(eventRegistry().objectCanDelete());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		db().delete(item);
		
		assertSingleObjectEventArgs(eventRegistry().objectCanDelete(), item);
		
		Assert.areSame(item, db().get(Item.class).next());
	}

	private void assertDeactivateEvent(Event4 event) throws Exception {
		listenToEvent(event);
		
		Item item = storeItem();
		db().deactivate(item, 1);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areEqual(0, item.id);
	}
	
	public void testObjectCanDeactivate() throws Exception {
		assertDeactivateEvent(eventRegistry().objectCanDeactivate());
	}
	
	public void testObjectOnDeactivate() throws Exception {
		assertDeactivateEvent(eventRegistry().objectOnDeactivate());
	}
	
	public void testCancellableObjectCanDeactivate() {
		listenToEvent(eventRegistry().objectCanDeactivate());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		db().deactivate(item, 1);
		
		assertSingleObjectEventArgs(eventRegistry().objectCanDeactivate(), item);
		
		Assert.areEqual(1, item.id);
	}
	
	private void assertSingleObjectEventArgs(Event4 expectedEvent, Item expectedItem) {
		Assert.areEqual(1, _recorder.size());
		EventRecord record = _recorder.get(0);
		Assert.areSame(expectedEvent, record.e);
		Assert.areSame(expectedItem, ((ObjectEventArgs)record.args).subject());
	}

	private EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
}
