/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.constraints;

import com.db4o.config.*;
import com.db4o.constraints.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class UniqueFieldIndexTestCase extends AbstractDb4oTestCase{
	
	public static void main(String[] arguments) {
		new UniqueFieldIndexTestCase().runClientServer();
	}
	
	public static class Item {
		
		public String	_str;

		public Item(){
		}
		
		public Item(String str){
			_str = str;
		}
		
	}
	
	protected void configure(Configuration config) {
		super.configure(config);
		config.objectClass(Item.class).objectField("_str").indexed(true);
		config.add(new UniqueFieldValueConstraint(Item.class, "_str"));
	}
	
	protected void store() throws Exception {
		store(new Item("1"));
		store(new Item("2"));
		store(new Item("3"));
	}
	
	public void testNewViolates(){
		store(new Item("2"));
		Assert.expect(UniqueFieldValueConstraintViolationException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().commit();
			}
		});
		db().rollback();
	}
	
	public void testUpdateViolates(){
		Query q = newQuery(Item.class);
		q.descend("_str").constrain("2");
		Item item = (Item) q.execute().next();
		item._str = "3";
		store(item);
		Assert.expect(UniqueFieldValueConstraintViolationException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().commit();
			}
		});
		db().rollback();
	}
	
	public void testUpdateDoesNotViolate(){
		Query q = newQuery(Item.class);
		q.descend("_str").constrain("2");
		Item item = (Item) q.execute().next();
		item._str = "4";
		store(item);
		db().commit();
	}
}
