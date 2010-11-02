/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.versant.*;
import com.db4o.foundation.*;

import db4ounit.*;

public abstract class VodProviderTestCaseBase  implements TestLifeCycle, ClassLevelFixtureTest  {
	
	protected static final String DATABASE_NAME = "VodProviderTestCaseBase";
	
	protected static VodDatabase _vod;
	
	protected VodReplicationProvider _provider;
	
	// This is a direct _VodJdo connection that works around our _provider 
	// so we can see what's committed, using a second reference system.
	protected VodJdoFacade _jdo;
	
	protected VodCobraFacade _cobra;
	
	public void setUp() {
		_jdo = VodJdo.createInstance(_vod);
		_cobra = VodCobra.createInstance(_vod);
		cleanDb();
		produceProvider();
	}

	protected void produceProvider() {
		if (_provider != null) {
			_provider.destroy();
		}
		_provider = new VodReplicationProvider(_vod, new JviDatabaseIdFactory(_vod));
	}

	public void tearDown() {
		_cobra.close();
		_jdo.close();
		destroyProvider();
	}

	protected void destroyProvider() {
		if (_provider == null) {
			return;
		}
		_provider.destroy();
		_provider = null;
	}
	
	private void cleanDb(){
		VodCobra.deleteAll(_vod);
	}
	
	public static void classSetUp() {
		if(_vod != null){
			throw new IllegalStateException();
		}
		_vod = new VodDatabase(DATABASE_NAME);
		_vod.removeDb();
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
	
	protected void withEventProcessor(Closure4<Void> closure) throws Exception {
		_vod.startEventProcessor();
		produceProvider();
		try {
			closure.run();
		} finally {
			destroyProvider();
			_vod.stopEventProcessor();
		}
	}

}
