/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import db4ounit.extensions.*;

public class DeleteSetTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new DeleteSetTestCase().runAll();
	}
	
	public static class Item {
		
	}
	
	protected void store() throws Exception {
		store(new Item());
	}
	
	public void test() throws Exception {
		Object item = retrieveOnlyInstance(Item.class);
		db().delete(item);
		db().set(item);
		db().commit();
		assertOccurrences(Item.class, 1);
	}

}
