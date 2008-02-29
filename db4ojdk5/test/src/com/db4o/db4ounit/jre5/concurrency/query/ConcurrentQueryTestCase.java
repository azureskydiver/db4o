/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.concurrency.query;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ConcurrentQueryTestCase extends Db4oClientServerTestCase {

	private static final int ITEM_COUNT = 100;

	public static final class Item {
		
		public int id;
		public Item parent;
		
		public Item() {
		}
		
		public Item(Item parent_, int id_) {
			id = id_;
			parent = parent_;
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		configLazyQueries(config);
	}

	private void configLazyQueries(Configuration config) {
		config.queries().evaluationMode(QueryEvaluationMode.LAZY);
	}
	
	protected void store() throws Exception {
		Item root = new Item(null, -1);
		for (int i=0; i<ITEM_COUNT; ++i) {
			store(new Item(root, i));
		}
	}
	
	public void conc(ExtObjectContainer client) {
		
		final ExtObjectContainer container = fileSession();
		final Item root = queryRoot(container);
		for (int i=0; i<100; ++i) {
			assertAllItems(queryItems(root, container));
		}
	}

	private Item queryRoot(final ExtObjectContainer container) {
		final Query q = itemQuery(container);
		q.descend("id").constrain(new Integer(-1));
		return (Item)q.execute().next();
	}

	private void assertAllItems(final Iterator result) {
		Collection4 found = collectItemIds(result);
		for (int expected=0; expected<ITEM_COUNT; ++expected) {
			found.remove(new Integer(expected));
		}
		Assert.areEqual("[]", found.toString());
	}

	private Collection4 collectItemIds(final Iterator result) {
		Collection4 found = new Collection4();
		for (int i=0; i<ITEM_COUNT; ++i) {
			final Item nextItem = (Item)result.next();
			found.add(new Integer(nextItem.id));
		}
		return found;
	}

	private Iterator queryItems(Item parent, ExtObjectContainer container) {
		final Query q = itemQuery(container);
		q.descend("parent").constrain(parent).identity();
		return q.execute().iterator();
	}

	private Query itemQuery(ExtObjectContainer container) {
		return newQuery(container, Item.class);
	}
}
