/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.header;

import com.db4o.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.OptOutCS;

public class ConfigurationSettingsTestCase extends AbstractDb4oTestCase
		implements OptOutCS {

	public void testChangingUuidSettings() throws Exception {

		fixture().config().generateUUIDs(0);

		reopen();

		Assert.areEqual(0, generateUUIDs());

		db().configure().generateUUIDs(-1);

		Assert.areEqual(-1, generateUUIDs());

		fixture().config().generateUUIDs(0);

		reopen();

		Assert.areEqual(0, generateUUIDs());

	}

	private int generateUUIDs() {
		return ((LocalObjectContainer) db()).config().generateUUIDs();
	}
}
