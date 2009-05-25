/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.events;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class UpdateInCallbackThrowsTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new UpdateInCallbackThrowsTestCase().runAll();
	}
	
	public static class Item {
		public String _name;
		public Item _child;

		public Item(String name) {
			this(name, null);
		}
		
		public Item(String name, Item child) {
			_name = name;
			_child = child;
		}
	}

	@Override
	protected void store() throws Exception {
		store(new Item("foo", new Item("bar")));
	}
	
	public void testReentrantUpdateAfterActivationThrows() {
		
		final Item foo = queryItemsByName("foo").next();
		db().deactivate(foo);
		
		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());
		registry.activated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4 e, ObjectInfoEventArgs args) {
				final Object obj = args.object();
				if (! (obj instanceof Item)) {
					return;
				}
				
				final Item item = (Item) obj;
				if (!item._name.equals("foo")) {
					return;
				}
					
				Assert.expect(IllegalStateException.class, new CodeBlock() { public final void run() {						
					item._child = new Item("baz");				
					store(item);					
				}});
			}
		});
		
		db().activate(foo, 1);
	}

	public void testReentrantUpdateThrows() {
		final ByRef<Boolean> activateRaised = new ByRef();
		activateRaised.value = false;
		
		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());
		registry.activated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4 e, ObjectInfoEventArgs args) {
				final Object obj = args.object();
				if (! (obj instanceof Item)) {
					return;
				}
				
				final Item item = (Item) obj;
				if (!item._name.equals("foo")) {
					return;
				}
				
				activateRaised.value = true;
					
				Assert.expect(IllegalStateException.class, new CodeBlock() { public final void run() {						
					item._child = new Item("baz");				
					store(item);					
				}});
			}
		});
		
		ObjectSet items = queryItemsByName("foo");
		Assert.areEqual(1, items.size());
		
		Assert.isFalse(activateRaised.value);
		
		try {
			items.next();
		} catch(DatabaseClosedException dce) {
			if (!isClientServer()) {
				throw dce;
			}
		}
	
		Assert.isTrue(activateRaised.value);		
	}

	private ObjectSet<Item> queryItemsByName(final String name) {
	    final Query query = newQuery(Item.class);
		query.descend("_name").constrain(name);
		return query.<Item>execute();
    }
}