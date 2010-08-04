/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;



import com.db4o.drs.versant.*;

import db4ounit.*;

public class VodDatabaseLifecycleTestCase implements TestCase {
	
	private static final String DATABASE_NAME = "VodDatabaseLifecycleTestCase"; 
	
	public void testLifeCycle(){
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		vod.removeDb();
		Assert.isFalse(vod.dbExists());
		vod.produceDb();
		Assert.isTrue(vod.dbExists());
		vod.removeDb();
		Assert.isFalse(vod.dbExists());
	}
	
	public void testEnhancer() throws Exception{
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		vod.enhance();
		
		// TODO: Test is some known class is PersistenceCapable
		
	}

}
