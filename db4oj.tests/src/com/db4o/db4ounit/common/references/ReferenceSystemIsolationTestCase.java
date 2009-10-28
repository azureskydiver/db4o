/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.db4ounit.common.references;

import com.db4o.cs.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;

/**
 * @sharpen.if !SILVERLIGHT
 */
public class ReferenceSystemIsolationTestCase extends TestWithTempFile {
	
	private static final String USERNAME = "db4o";
	
	private static final String PASSWORD = "db4o";

	public static class Item{
		
	}
	
	public void test(){
		
		ExtObjectServer server = Db4oClientServer.openServer(tempFile(), Db4oClientServer.ARBITRARY_PORT).ext();
		server.grantAccess(USERNAME, PASSWORD);
		
		ExtObjectContainer client = Db4oClientServer.openClient("localhost", server.port(), USERNAME, PASSWORD).ext();
		ObjectContainerSession embeddedClient = (ObjectContainerSession) server.openClient().ext();
		
		try{
		
			Item item = new Item();
			client.store(item);
			int id = (int) client.getID(item);
			
			Query query = client.query();
			query.constrain(Item.class);
			query.constrain(new Evaluation() {
				public void evaluate(Candidate candidate) {
					candidate.include(true);
				}
			});
			query.execute();
			
			Assert.isNull(embeddedClient.transaction().referenceForId(id));
		} finally {
			embeddedClient.close();
			client.close();
			server.close();
		}
	}

}
