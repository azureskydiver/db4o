/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.classindex;

import com.db4o.db4ounit.common.btree.ExpectingVisitor;
import com.db4o.inside.*;
import com.db4o.inside.classindex.ClassIndexStrategy;

import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.*;

public class ClassIndexTestCase extends AbstractDb4oTestCase implements OptOutCS{
	
	public static class Item {
		public String name;

		public Item(String _name) {
			this.name = _name;
		}
	}
	
	public static void main(String[] args) {
		new ClassIndexTestCase().runSolo();
	}
	
	public void testDelete() throws Exception {
		Item item = new Item("test");
		store(item);
		int id=(int)db().getID(item);
		assertID(id);

		reopen();
		
		item=(Item)db().get(item).next();
		id=(int)db().getID(item);
		assertID(id);
		
		db().delete(item);
		db().commit();
		assertEmpty();
		
		reopen();

		assertEmpty();
	}

	private void assertID(int id) {
		assertIndex(new Object[]{new Integer(id)});
	}

	private void assertEmpty() {
		assertIndex(new Object[]{});
	}

	private void assertIndex(Object[] expected) {
		YapClass clazz = stream().getYapClass(reflector().forClass(Item.class));
		ExpectingVisitor visitor = new ExpectingVisitor(expected);
		ClassIndexStrategy index = clazz.index();
		index.traverseAll(trans(),visitor);
		visitor.assertExpectations();
	}
}
