/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.header;

import com.db4o.config.*;
import com.db4o.inside.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.OptOutCS;

public class SimpleTimeStampIdTestCase extends AbstractDb4oTestCase implements
		OptOutCS {

	public static void main(String[] arguments) {
		new SimpleTimeStampIdTestCase().runSolo();
	}

	public static class STSItem {

		public String _name;

		public STSItem() {
		}

		public STSItem(String name) {
			_name = name;
		}
	}

	protected void configure(Configuration config) {
		ObjectClass objectClass = config.objectClass(STSItem.class);
		objectClass.generateUUIDs(true);
		objectClass.generateVersionNumbers(true);
	}

	protected void store() {
		db().set(new STSItem("one"));
	}

	public void test() throws Exception {
		STSItem item = (STSItem) db().get(STSItem.class).next();

		long version = db().getObjectInfo(item).getVersion();
		Assert.isGreater(0, version);
		Assert.isGreaterOrEqual(version, currentVersion());

		reopen();

		STSItem item2 = new STSItem("two");
		db().set(item2);

		long secondVersion = db().getObjectInfo(item2).getVersion();

		Assert.isGreater(version, secondVersion);
		Assert.isGreaterOrEqual(secondVersion, currentVersion());
	}

	private long currentVersion() {
		return ((YapFile) db()).currentVersion();
	}
}
