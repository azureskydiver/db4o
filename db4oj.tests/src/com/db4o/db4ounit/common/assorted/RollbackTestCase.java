/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import db4ounit.*;
import db4ounit.extensions.*;


public class RollbackTestCase extends AbstractDb4oTestCase{
	
	public static class Item{
		
		public String _string;
		
		public Item() {
		}
		
		public Item(String str) {
			_string = str;
		}
		
	}

	public static void main(String[] args) {
		new RollbackTestCase().runClientServer();
	}
	
	public void testNotIsStoredOnRollback(){
		Item item = new Item();
		store(item);
		db().rollback();
		Assert.isFalse(db().isStored(item));
	}

}
