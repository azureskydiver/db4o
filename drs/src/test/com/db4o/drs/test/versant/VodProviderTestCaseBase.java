/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.File;

import com.db4o.drs.versant.JdoMetadataGenerator;
import com.db4o.drs.versant.JviDatabaseIdFactory;
import com.db4o.drs.versant.VodDatabase;
import com.db4o.drs.versant.VodJdo;
import com.db4o.drs.versant.VodJdoFacade;
import com.db4o.drs.versant.VodReplicationProvider;
import com.db4o.drs.versant.metadata.CobraPersistentObject;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent;

import db4ounit.ClassLevelFixtureTest;
import db4ounit.TestLifeCycle;

public abstract class VodProviderTestCaseBase  implements TestLifeCycle, ClassLevelFixtureTest  {
	
	protected static final String DATABASE_NAME = "VodProviderTestCaseBase";
	
	protected static VodDatabase _vod;
	
	protected VodReplicationProvider _provider;
	
	// This is a direct _VodJdo connection that works around our _provider 
	// so we can see what's committed, using a second reference system.
	protected VodJdoFacade _jdo;
	
	protected abstract Class[] persistedClasses();
	
	public void setUp() {
		_jdo = VodJdo.createInstance(_vod);
		cleanDb();
		produceProvider();
	}

	protected void produceProvider() {
		if (_provider != null) {
			return;
		}
		_provider = new VodReplicationProvider(_vod, new JviDatabaseIdFactory(_vod));
	}

	public void tearDown() {
		_jdo.close();
		destroyProvider();
	}

	protected void destroyProvider() {
		if (_provider == null) {
			return;
		}
		_provider.commit();
		_provider.destroy();
		_provider = null;
	}
	
	private void cleanDb(){
		for(Class c : persistedClasses()) {
			_jdo.deleteAll(c);
		}
		_jdo.deleteAll(CobraPersistentObject.class);
		_jdo.deleteAll(ObjectLifecycleEvent.class);
		_jdo.commit();
	}
	
	public static void classSetUp() {
		if(_vod != null){
			throw new IllegalStateException();
		}
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.produceDb();
		JdoMetadataGenerator generator = new JdoMetadataGenerator(new File("bin"));
		_vod.addJdoMetaDataFile(generator.resourcePath(generator.generate("com.db4o.drs.test.versant.data")));
		_vod.enhance();
		_vod.createEventSchema();
		_vod.startEventDriver();
	}

	public static void classTearDown() {
		_vod.stopEventDriver();
		_vod.removeDb();
		_vod = null;
	}

}
