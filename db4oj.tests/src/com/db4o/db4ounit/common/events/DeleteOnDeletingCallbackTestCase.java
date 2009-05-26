package com.db4o.db4ounit.common.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeleteOnDeletingCallbackTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
	}
	
	public static class RootItem {
		
		public Item child;
		
		public RootItem() {
		}
		
		public void objectOnDelete(ObjectContainer container) {
			container.delete(child);
		}
	}
	
	@Override
	protected void store() throws Exception {
		store(new RootItem());
	}
	
	public void test() throws Exception {
		
		final RootItem root = retrieveOnlyInstance(RootItem.class);
		root.child = new Item();
		db().store(root);
		db().delete(root);
		reopen();
		assertClassIndexIsEmpty();
	}

	private void assertClassIndexIsEmpty() {
	    Iterator4Assert.areEqual(new Object[0], getAllIds());
    }

	private IntIterator4 getAllIds() {
	    return fileSession().getAll(fileSession().transaction(), QueryEvaluationMode.IMMEDIATE).iterateIDs();
    }

}
