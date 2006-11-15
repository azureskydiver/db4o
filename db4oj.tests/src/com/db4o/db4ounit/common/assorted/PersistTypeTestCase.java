/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public class PersistTypeTestCase extends AbstractDb4oTestCase {

	public static final class Item {
		public Class type;
		
		public Item() {			
		}
		
		public Item(Class type_) {
			type = type_;
		}
	}
	
	protected void store() throws Exception {
		store(new Item(String.class));
	}
	
	public void test() {
		Assert.areEqual(String.class, ((Item)retrieveOnlyInstance(Item.class)).type);
	}
}
