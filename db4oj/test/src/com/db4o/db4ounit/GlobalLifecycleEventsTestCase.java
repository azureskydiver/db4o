package com.db4o.db4ounit;

import com.db4o.events.*;

import db4ounit.Assert;
import db4ounit.db4o.Db4oTestCase;

public class GlobalLifecycleEventsTestCase extends Db4oTestCase {
	
	private EventRecorder _recorder;

	public void testObjectCanNew() {
		
		Item item = new Item(1);
		db().set(item);
		
		assertSingleObjectEventArgs(item);
		
		Assert.areSame(item, db().get(Item.class).next());
	}	
	
	public void testCancellableObjectCanNew() {	
		
		_recorder.cancel(true);
		
		Item item = new Item(1);
		db().set(item);
		
		assertSingleObjectEventArgs(item);
		
		Assert.areEqual(0, db().get(Item.class).size());
	}
	
	private void assertSingleObjectEventArgs(Item expectedItem) {
		Assert.areEqual(1, _recorder.size());
		Assert.areSame(expectedItem, ((ObjectEventArgs)_recorder.get(0).args).subject());
	}

	public void setUp() throws Exception {
		super.setUp();
		_recorder = new EventRecorder(); 
		eventRegistry().objectCanNew().addListener(_recorder);
	}

	private EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
}
