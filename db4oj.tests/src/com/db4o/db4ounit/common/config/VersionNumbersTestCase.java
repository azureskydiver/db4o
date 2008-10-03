/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.config;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class VersionNumbersTestCase extends AbstractDb4oTestCase{
	
	public static class Item {
		public String _name;
	}
	
	protected void configure(Configuration config) throws Exception {
		config.generateVersionNumbers(ConfigScope.GLOBALLY);
	}
	
	protected void store() throws Exception {
		Item item = new Item();
		item._name = "original";
		store(item);
	}
	
	public void test(){
		Item item = (Item) retrieveOnlyInstance(Item.class);
		ObjectInfo objectInfo = db().getObjectInfo(item);
		long version1 = objectInfo.getVersion();
		item._name = "modified";
		db().store(item);
		db().commit();
		long version2 = objectInfo.getVersion();
		Assert.isGreater(version1, version2);
		db().store(item);
		db().commit();
		objectInfo = db().getObjectInfo(item);
		long version3 = objectInfo.getVersion();
		Assert.isGreater(version2, version3);
	}

}
