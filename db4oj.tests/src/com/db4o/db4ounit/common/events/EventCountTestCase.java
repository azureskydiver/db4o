/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.foundation.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oClientServerFixture;

public class EventCountTestCase extends AbstractDb4oTestCase {

	private static final int MAX_CHECKS = 10;
	private static final long WAIT_TIME = 10;
	private IntByRef _activated = new IntByRef(0);
	private IntByRef _updated = new IntByRef(0);
	private IntByRef _deleted = new IntByRef(0);
	private IntByRef _created = new IntByRef(0);
	private IntByRef _committed = new IntByRef(0);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new EventCountTestCase().runAll();
	}

	public void testEventRegistryCounts() throws Exception {
		registerEventHandlers();

		for (int i = 0; i < 1000; i++) {
			Item item = new Item(i);
			db().store(item);
			Assert.isTrue(db().isStored(item));

			if (((i + 1) % 100) == 0) {
				db().commit();
			}
		}

		assertCount(_created, 1000, "created");
		assertCount(_committed, 10, "commit");

		reopenAndRegister();

		ObjectSet items = newQuery(Item.class).execute();
		Assert.areEqual(1000, items.size(), "Wrong number of objects retrieved");
		
		while (items.hasNext()) {
			Item item = (Item) items.next();
			item._value++;
			store(item);
		}

		assertCount(_activated, 1000, "activated");
		assertCount(_updated, 1000, "updated");

		items.reset();		
		while (items.hasNext()) {

			Object item = items.next();
			db().delete(item);
			Assert.isFalse(db().isStored(item));
		}

		assertCount(_deleted, 1000, "deleted");
	}

	private void assertCount(IntByRef ref, int expected, String name) throws InterruptedException {
		for(int checkCount = 0; checkCount < MAX_CHECKS; checkCount++) {
			synchronized(ref) {
				if(ref.value == expected) {
					break;
				}
				ref.wait(WAIT_TIME);
			}
		}
		Assert.areEqual(expected, ref.value, "Incorrect count for " + name);
	}
	
	private void reopenAndRegister() throws Exception {
		reopen();
		registerEventHandlers();
	}

	private void registerEventHandlers() {
		ObjectContainer deletionEventSource = db();
		if (fixture() instanceof Db4oClientServerFixture) {
			Db4oClientServerFixture clientServerFixture = (Db4oClientServerFixture) fixture();
			deletionEventSource = clientServerFixture.server().ext().objectContainer();
		}
		
		EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(db());
		EventRegistry deletionEventRegistry = EventRegistryFactory.forObjectContainer(deletionEventSource);

		// No dedicated IncrementListener class due to sharpen event semantics
		
		deletionEventRegistry.deleted().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				increment(_deleted);
			}
		});		
		eventRegistry.activated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				increment(_activated);
			}
		});
		eventRegistry.committed().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				increment(_committed);
			}
		});
		eventRegistry.created().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				increment(_created);
			}
		});
		eventRegistry.updated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				increment(_updated);
			}
		});
	}

	public static class Item {
		public Item(int i) {
			_value = i;
		}

		public int _value;
	}
	
	static void increment(IntByRef ref) {
		synchronized(ref) {
			ref.value++;
			ref.notifyAll();
		}
	}
	
}
