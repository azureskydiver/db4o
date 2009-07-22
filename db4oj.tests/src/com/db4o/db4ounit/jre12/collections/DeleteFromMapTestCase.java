/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeleteFromMapTestCase extends AbstractDb4oTestCase{
	
	public static class Holder {
		public Map<Item, Item> _map = new HashMap<Item, Item>();
	}
	
	public static class Item {
		
	}
	
	@Override
	protected void store() throws Exception {
		Holder holder = new Holder();
		Item item = new Item();
		holder._map.put(item, item);
		store(holder);
	}
	
	public void test(){
		Item item = retrieveOnlyInstance(Item.class);
		db().delete(item);
		db().commit();
		Holder holder = retrieveOnlyInstance(Holder.class);
		Assert.areEqual(0, holder._map.size());
	}

}
