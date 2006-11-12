/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnDelete extends AbstractDb4oTestCase {
	
	public static class Item {
		public String item;
	}
	
	public Item[] items;
	
	public void test() throws Exception {
		noAccidentalDeletes();
	}
	
	private void noAccidentalDeletes() throws Exception {
	 	noAccidentalDeletes1(true, true);
	 	noAccidentalDeletes1(true, false);
	 	noAccidentalDeletes1(false, true);
	 	noAccidentalDeletes1(false, false);
	}
	
	private void noAccidentalDeletes1(boolean cascadeOnUpdate, boolean cascadeOnDelete) throws Exception {
		deleteAll(getClass());
		deleteAll(Item.class);
		
		ObjectClass oc = Db4o.configure().objectClass(CascadeOnDelete.class);
		oc.cascadeOnDelete(cascadeOnDelete);
		oc.cascadeOnUpdate(cascadeOnUpdate);
		
		reopen();
		
		Item i = new Item();
		CascadeOnDelete cod = new CascadeOnDelete();
		cod.items = new Item[]{ i };
		db().set(cod);
		db().commit();
		
		cod.items[0].item = "abrakadabra";
		db().set(cod);
		if(! cascadeOnDelete && ! cascadeOnUpdate){
			// the only case, where we don't cascade
			db().set(cod.items[0]);
		}
		
		Assert.areEqual(1, countOccurences(Item.class));
		db().commit();
		Assert.areEqual(1, countOccurences(Item.class));
	}
}
