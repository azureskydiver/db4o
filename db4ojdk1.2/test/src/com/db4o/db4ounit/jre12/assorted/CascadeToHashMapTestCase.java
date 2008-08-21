/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.assorted;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.test.*;

import db4ounit.extensions.*;

public class CascadeToHashMapTestCase extends AbstractDb4oTestCase{
	
	public static class Item {
		public HashMap hm;
	}
	
	protected void configure(Configuration config) throws Exception {
		
		config.weakReferences(false);

		config.generateUUIDs(Integer.MAX_VALUE);
		config.generateVersionNumbers(Integer.MAX_VALUE);
		
		config.objectClass(Item.class).cascadeOnUpdate(true);
		config.objectClass(Item.class).cascadeOnDelete(true);
	}

	public void store() {
		
		Item item = new Item();
		item.hm = new HashMap();
		item.hm.put("key1", new Atom("stored1"));
		item.hm.put("key2", new Atom(new Atom("storedChild1"), "stored2"));
		store(item);
		
		store(new Atom());
	}

	public void test() {
		assertOccurrences(HashMap.class, 1);
		assertOccurrences(Atom.class, 4);
		deleteAll(Item.class);
		db().commit();
		assertOccurrences(Atom.class, 2);
	}
	
	public static void main(String[] arguments) {
		new CascadeToHashMapTestCase().runClientServer(false); 
	}

}
