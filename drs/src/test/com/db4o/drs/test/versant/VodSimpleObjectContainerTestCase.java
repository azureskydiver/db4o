/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;

import db4ounit.*;

public class VodSimpleObjectContainerTestCase implements TestLifeCycle{
	
	private static final String DATABASE_NAME = "SimpleObjectContainer";
	
	private VodDatabase _vod;	
	
	private VodReplicationProvider _provider;
	
	public void setUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.createDb();
		_vod.amendPropertyIfNotExists("versant.metadata.0", "drs.jdo");
		_vod.enhance("bin");
		_provider = new VodReplicationProvider(_vod);
	}

	public void tearDown() throws Exception {
		_vod.removeDb();
	}
	
	public void testStoreNew(){
		_provider.storeNew(new Item("one"));
		_provider.commit();
		ObjectSet storedObjects = _provider.getStoredObjects(Item.class);
		Assert.areEqual(1, storedObjects.size());
	}

}
