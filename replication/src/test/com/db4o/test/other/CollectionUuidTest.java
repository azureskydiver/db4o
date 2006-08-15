package com.db4o.test.other;

import java.util.ArrayList;

import com.db4o.Db4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.replication.db4ounit.DrsTestCase;

import db4ounit.Assert;
import db4ounit.db4o.Db4oTestCase;

public class CollectionUuidTest extends DrsTestCase {	
	
	protected void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
	}
	
	public void test() {
		ExtObjectContainer oc = a().db();

		ArrayList list = new ArrayList();
		oc.set(list);
		oc.commit();
		Assert.isNotNull(oc.getObjectInfo(list).getUUID());
	}
}
