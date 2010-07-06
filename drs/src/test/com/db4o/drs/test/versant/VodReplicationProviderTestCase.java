/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import javax.jdo.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;

import db4ounit.*;

public class VodReplicationProviderTestCase implements TestLifeCycle {
	
	private static final String DATABASE_NAME = "VodDatabaseTestCase";
	
	private VodDatabase _vod;	
	
	// This is a direct PersistenceManager that works around the _provider
	// so we can see what's committed, using a second reference system.
	private PersistenceManager _pm;
	
	TestableReplicationProviderInside _provider;

	
	public void setUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.createDb();
		_vod.amendPropertyIfNotExists("versant.metadata.0", "drs.jdo");
		_vod.enhance("bin");
		_provider = new VodReplicationProvider(_vod);
		_pm = _vod.createPersistenceManager();
	}

	public void tearDown() throws Exception {
		_pm.close();
		
		_provider.destroy();
		_vod.removeDb();
		_vod = null;
	}

	public void testReplicationReference(){
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		
		ReplicationReference replicationReferenceOnFirstStore = _provider.produceReference(item);
		Assert.isNotNull(replicationReferenceOnFirstStore);
		Assert.areSame(item, replicationReferenceOnFirstStore.object());
		
		ReplicationReference secondReferenceOnFirstStore = _provider.produceReference(item);
		Assert.areSame(replicationReferenceOnFirstStore, secondReferenceOnFirstStore);
		
		long versionOnStore = replicationReferenceOnFirstStore.version();
		
		Assert.isGreater(0, versionOnStore);
		
		item.name("modified");
		_provider.update(item);
		_provider.commit();
		
		_provider.clearAllReferences();
		
		ReplicationReference replicationReferenceOnUpdate = _provider.produceReference(item);
		long versionOnUpdate = replicationReferenceOnUpdate.version();
		Assert.isGreater(versionOnStore, versionOnUpdate);
	}

}
