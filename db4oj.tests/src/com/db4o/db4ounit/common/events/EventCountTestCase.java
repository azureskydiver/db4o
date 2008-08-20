/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oClientServerFixture;

public class EventCountTestCase extends AbstractDb4oTestCase {

	private int _activated;
	private int _updated;
	private int _deleted;
	private int _created;
	private int _committed;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new EventCountTestCase().runAll();
	}

	public void testEventRegistryStress() throws Exception {
		registerEventHandlers();

		for (int i = 0; i < 1000; i++) {
			Item item = new Item(i);
			db().store(item);
			Assert.isTrue(db().isStored(item));

			if ((i % 100) == 9) {
				db().commit();
			}
		}

		Assert.areEqual(1000, _created, "The counted number of created objects is not correct");
		Assert.areEqual(10, _committed, "The counted number of committed objects is not correct");

		reopenAndRegister();

		ObjectSet items = newQuery(Item.class).execute();
		Assert.areEqual(1000, items.size(), "Wrong number of objects retrieved");
		
		while (items.hasNext()) {
			Item item = (Item) items.next();
			item._value++;
			store(item);
		}

		Assert.areEqual(1000, _activated, "The counted number of activated objects is not correct");
		Assert.areEqual(1000, _updated, "The counted number of updated objects is not correct");

		items.reset();		
		while (items.hasNext()) {

			Object item = items.next();
			db().delete(item);
			Assert.isFalse(db().isStored(item));
		}

		Assert.areEqual(1000, _deleted, "The counted number of deleted objects is not correct");
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

		deletionEventRegistry.deleted().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_deleted++;
			}
		});		

		eventRegistry.activated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_activated++;
			}
		});

		eventRegistry.committed().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_committed++;
			}
		});

		eventRegistry.created().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_created++;
			}
		});

		eventRegistry.updated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_updated++;
			}
		});
	}

	public static class Item {
		public Item(int i) {
			_value = i;
		}

		public int _value;
	}
}
