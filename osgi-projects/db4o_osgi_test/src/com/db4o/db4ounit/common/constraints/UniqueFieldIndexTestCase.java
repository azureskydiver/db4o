/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.db4ounit.common.constraints;

import com.db4o.config.*;
import com.db4o.constraints.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class UniqueFieldIndexTestCase extends AbstractDb4oTestCase{
	
	public static void main(String[] arguments) {
		new UniqueFieldIndexTestCase().runAll();
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
