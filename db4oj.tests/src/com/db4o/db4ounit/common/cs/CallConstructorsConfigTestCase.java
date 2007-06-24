/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;

import db4ounit.*;


public class CallConstructorsConfigTestCase extends StandaloneCSTestCaseBase {
	
	protected void runTest() {
		withClient(new ClientBlock() {
			public void run(ObjectContainer client) {	
				client.set(new Item());
			}
		});
		
		withClient(new ClientBlock() {
			public void run(ObjectContainer client) {	
				Assert.areEqual(1, client.query(Item.class).size());
			}
		});
	}

	protected void configure(final Configuration config) {
		config.callConstructors(true);
		config.exceptionsOnNotStorable(true);
	}

}
