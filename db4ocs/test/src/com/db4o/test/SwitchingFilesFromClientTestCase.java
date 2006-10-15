/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.File;

import com.db4o.ext.ExtClient;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.config.TestConfigure;
import com.db4o.test.persistent.SimpleObject;

import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class SwitchingFilesFromClientTestCase extends ClientServerTestCase {

	static final String DB_FILE = "switchedToTest.yap";

	public String name;

	public void store(ExtObjectContainer oc) {
		new File(DB_FILE).delete();
	}

	public void conc(ExtObjectContainer oc) {
		ExtClient client = (ExtClient) oc;
		// FIXME: never end up.
		client.switchToFile(DB_FILE);
		client.switchToMainFile();
		oc.set(new SimpleObject("hello", 1));
	}

	public void check(ExtObjectContainer oc) {
		Db4oUtil.assertOccurrences(oc, SimpleObject.class,
				TestConfigure.CONCURRENCY_THREAD_COUNT);
	}
}
