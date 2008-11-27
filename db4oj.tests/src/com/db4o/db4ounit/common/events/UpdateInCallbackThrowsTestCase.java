/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.events;

import com.db4o.ObjectSet;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.events.ObjectEventArgs;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.foundation.ByRef;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.CodeBlock;
import db4ounit.extensions.AbstractDb4oTestCase;

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

	public void testReentrantUpdateThrows() {
		final ByRef activateRaised = new ByRef();
		activateRaised.value = false;
		
		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());
		registry.activated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				final Object obj = ((ObjectEventArgs) args).object();
				if (! (obj instanceof Item)) {
					return;
				}
				
				final Item item = (Item) obj;
				
				if (item._name.equals("foo")) {
					activateRaised.value = true;
					
					Assert.expect(IllegalStateException.class, new CodeBlock(){
						public final void run() {						
							item._child = new Item("baz");				
							store(item);					
						}
					});					
				}
			}
		});
		
		final Query query = newQuery(Item.class);
		query.descend("_name").constrain("foo");
		
		ObjectSet items = query.execute();
		Assert.areEqual(1, items.size());
		
		
		try {
			items.next();
		}
		catch(DatabaseClosedException dce) {
			if (!isClientServer()) {
				throw dce;
			}
		}
	
		Assert.isTrue((Boolean) activateRaised.value);		
	}
}