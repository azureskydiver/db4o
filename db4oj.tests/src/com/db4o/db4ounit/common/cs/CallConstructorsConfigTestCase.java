/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.Configuration;
import com.db4o.foundation.io.*;

import db4ounit.*;


public class CallConstructorsConfigTestCase implements TestCase {
	
	public static final class Item {
	}
	
	public void test() {
		final Configuration config = Db4o.newConfiguration();
		config.callConstructors(true);
		config.exceptionsOnNotStorable(true);
		
		final ObjectServer server = Db4o.openServer(config, databaseFile(), 1022);
		try {
			server.grantAccess("db4o", "db4o");
			
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
			
		} finally {
			server.close();
			File4.delete(databaseFile());
		}
	}

	private String databaseFile() {
		return Path4.combine(Path4.getTempPath(), "cc.db4o");
	}
	
	public interface ClientBlock {
		void run(ObjectContainer client);
	}

	private void withClient(ClientBlock block) {
		final ObjectContainer client = Db4o.openClient("localhost", 1022, "db4o", "db4o");
		try {
			block.run(client);			
		} finally {
			client.close();
		}
	}

}
