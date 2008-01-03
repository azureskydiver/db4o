/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre11.concurrency.staging;

import java.io.*;

import com.db4o.db4ounit.common.persistent.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

public class SwitchingFilesFromClientTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new SwitchingFilesFromClientTestCase().runConcurrency();
	}
	
	static final String DB_FILE = "switchedToTest.yap";

	public String name;

	protected void store(ExtObjectContainer oc) {
		new File(DB_FILE).delete();
	}

	public void conc(ExtObjectContainer oc) {
		ExtClient client = (ExtClient) oc;
		client.switchToFile(DB_FILE);
		client.switchToMainFile();
		client.store(new SimpleObject("hello", 1));
	}

}
