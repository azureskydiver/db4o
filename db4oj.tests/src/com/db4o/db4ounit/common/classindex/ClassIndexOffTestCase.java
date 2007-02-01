/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.classindex;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.inside.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class ClassIndexOffTestCase extends AbstractDb4oTestCase implements OptOutCS{
	
	public static class Item {
		public String name;

		public Item(String _name) {
			this.name = _name;
		}
	}
	
	public static void main(String[] args) {
		new ClassIndexOffTestCase().runSolo();
	}
	
	protected void configure(Configuration config) {
		super.configure(config);
		config.objectClass(Item.class).indexed(false);
	}
	
	public void test(){
		db().set(new Item("1"));
		ClassMetadata yc = (ClassMetadata)db().storedClass(Item.class);
		Assert.isFalse(yc.hasIndex());
		
		assertNoItemFound();
		
		db().commit();
		assertNoItemFound();
	}
	
	private void assertNoItemFound(){
		Query q = db().query();
		q.constrain(Item.class);
		Assert.areEqual(0, q.execute().size());
	}

}
