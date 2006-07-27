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

	private void assertNewEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = new Item(1);
		db().set(item);
		
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

	private void storeAndReopen() throws Exception {
		db().set(new Item(1));
		reopen();
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
		
		Item item = new Item(1);
		db().set(item);
		
		assertSingleObjectEventArgs(eventRegistry().objectCanNew(), item);
		
		Assert.areEqual(0, db().get(Item.class).size());
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
