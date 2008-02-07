/* Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.tp;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.query.Query;
import com.db4o.ta.RollbackStrategy;
import com.db4o.ta.TransparentPersistenceSupport;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class DeactivateDeletedObjectOnRollbackStrategyTestCase extends
		AbstractDb4oTestCase {
	
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		
		config.add(
				new TransparentPersistenceSupport(
						new RollbackStrategy() {
							public void rollback(ObjectContainer container, Object obj) {
								container.ext().deactivate(obj);
							}
						})
				);						
	}
	
	protected void store() throws Exception {
		db().store(new Item("foo.tbd"));
	}
	
	public void test() {
		Item tbd = insertAndRetrieve();
		
		tbd.setName("foo.deleted");		
		db().delete(tbd);
		
		db().rollback();
		Assert.areEqual("foo.tbd", tbd.getName());
	}

	private Item insertAndRetrieve() {
		Query query = newQuery(Item.class);
		query.descend("name").constrain("foo.tbd");		
		ObjectSet set = query.execute();
		Assert.areEqual(1, set.size());
		
		return (Item) set.next();
	}
	
	public static void main(String[] args) {
		new DeactivateDeletedObjectOnRollbackStrategyTestCase().runAll();
	}
}
