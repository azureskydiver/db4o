/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.activation;

import com.db4o.config.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class IntTransparentActivationDepthTestCase extends AbstractDb4oTestCase implements OptOutCS{
	public static void main(String[] args) {
		new IntTransparentActivationDepthTestCase().runAll();
	}

	protected void configure(Configuration config) throws Exception {
		config.activationDepth(0);
		super.configure(config);
	}

	public static class Item {
		public int value;

		public LinkedItemList list;

		public Item() {

		}
	}

	protected void store() throws Exception {
		Item item = new Item();
		item.value = 42;
		item.list = LinkedItemList.newList(5);
		store(item);
	}

	public void test() throws Exception {
		Item item = (Item) retrieveOnlyInstance(Item.class);
		asertNullItem(item);
		ObjectReference itemRef = trans().referenceForObject(item);
		itemRef.activate();
		Assert.areEqual(42, item.value);
		Assert.isNotNull(item.list);
		Assert.isNull(item.list.next);
	}

	private void asertNullItem(Item item) {
		Assert.areEqual(0, item.value);
		Assert.isNull(item.list);
	}

}
