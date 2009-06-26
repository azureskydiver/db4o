/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;

public class TransparentActivationTestCase extends DrsTestCase{
	
	public static class Item implements Activatable{
		
		private String _name;
		
		private transient Activator _activator;

		public Item(String name){
			_name = name;
		}
		
		public void activate(ActivationPurpose purpose) {
			if(_activator != null){
				_activator.activate(purpose);
			}
		}

		public void bind(Activator activator) {
			_activator = activator;
		}

		public Object name() {
			return _name;		
		}
		
	}
	
	@Override
	protected void configure(Configuration config) {
		config.add(new TransparentActivationSupport());
	}
	
	public void test() throws Exception{
		
		Item item = new Item("foo");
		a().provider().storeNew(item);
		reopen();
		replicateAll(a().provider(), b().provider());
		ObjectSet items = b().provider().getStoredObjects(Item.class);
		Assert.isTrue(items.hasNext());
		Item replicatedItem = (Item) items.next();
		Assert.areEqual(item.name(), replicatedItem.name());
	
		
		
	}

}
