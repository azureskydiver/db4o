/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.constraints;

import java.util.*;

import com.db4o.config.*;
import com.db4o.constraints.*;

import db4ounit.extensions.*;

public class UniqueFieldValueDoesNotThrows extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new UniqueFieldValueDoesNotThrows().runAll();
	}
	
	public static class Item {
		
		public Long id;
		
		public String name;
		
		public Item(){
			
		}
		
		public Item(int id, String name) {
			this.id = new Long(id);
			this.name = name;
		}
		
		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}
	
	public static class Holder {
		
		public HashMap<Item, Long> _items = new HashMap<Item, Long>();		
		
		public void add(Item item) {
			_items.put(item, item.id);
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).objectField("id").indexed(true);
		
		config.add(new UniqueFieldValueConstraint(Item.class, "id"));
		config.objectClass(Holder.class).callConstructor(true);
	}
	
	public void test() throws Exception {
		store(newHolder("foo", "bar"));
		db().commit();
	}


	private Object newHolder(String... names) {
		final Holder holder = new Holder();
		for(int i = 0; i < names.length; i++) {
			holder.add(new Item(i, names[i]));
		}
		
		return holder;
	}
}
