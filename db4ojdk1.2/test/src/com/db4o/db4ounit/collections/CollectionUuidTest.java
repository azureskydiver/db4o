package com.db4o.db4ounit.collections;

import java.util.ArrayList;

import com.db4o.Db4o;

import db4ounit.Assert;
import db4ounit.extensions.Db4oTestCase;

public class CollectionUuidTest extends Db4oTestCase {	
	
	protected void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
	}
	
	public void test() {
		ArrayList list = new ArrayList();
		db().set(list);
		db().commit();
		Assert.isNotNull(db().getObjectInfo(list).getUUID());
	}
}
