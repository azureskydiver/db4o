/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;
import com.db4o.ext.ObjectInfo;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @exclude
 */
public class CommitTimeCallbacksTestCase extends AbstractDb4oTestCase {
	
	public static final class Item {
		public int id;
		
		public Item() {
		}
		
		public Item(int id_) {
			id = id_;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CommitTimeCallbacksTestCase().runSolo();
	}
	
	public void testCommitting() {
		
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(db());
		final EventRecorder recorder = new EventRecorder();
		registry.committing().addListener(recorder);
		
		final Item item1 = new Item(1);
		final Item item2 = new Item(2);
		db().set(item1);
		db().set(item2);
		
		Assert.areEqual(0, recorder.size());
		
		db().commit();
		Assert.areEqual(1, recorder.size());
		
		Assert.areSame(registry.committing(), recorder.get(0).e);
		
		CommitEventArgs args = (CommitEventArgs)recorder.get(0).args;
		ObjectInfo[] added = args.added();
		Assert.areEqual(2, added.length);
		assertContains(item1, added);
		assertContains(item2, added);
		
	}

	private void assertContains(Item expectedItem, ObjectInfo[] items) {
		for (int i = 0; i < items.length; i++) {
			ObjectInfo info = items[i];
			if (expectedItem == info.getObject()) {
				return;
			}
		}
		Assert.fail("Object '" + expectedItem + "' not found.");
	}

}
