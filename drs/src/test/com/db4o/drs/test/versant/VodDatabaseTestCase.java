/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import javax.jdo.*;

import com.db4o.drs.versant.*;

import db4ounit.*;

public class VodDatabaseTestCase extends VodDatabaseTestCaseBase implements TestLifeCycle {
	
	private static final String DATABASE_NAME = "VodDatabaseTestCase";
	
	private VodDatabase _vod;

	public void setUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.createDb();
		
	}

	public void tearDown() throws Exception {
		_vod.removeDb();
	}
	
	public void testPersistenceManagerFactory(){
		registerMetadataFile(_vod);
		PersistenceManager pmf = _vod.createPersistenceManager();
		Assert.isFalse(pmf.isClosed());
		pmf.close();
	}
	
	

}
