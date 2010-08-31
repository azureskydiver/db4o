/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;
import java.util.*;

import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.ClientChannelControl;
import com.db4o.drs.versant.ipc.*;

import db4ounit.*;

public class VodProviderTestCaseBase  implements TestLifeCycle, ClassLevelFixtureTest  {
	
	private boolean EXPOSE_OBJECT_DELETE_BUG = false;
	
	protected static final String DATABASE_NAME = "VodProviderTestCaseBase";
	
	protected static VodDatabase _vod;
	
	protected VodReplicationProvider _provider;
	
	// This is a direct _VodJdo connection that works around our _provider 
	// so we can see what's committed, using a second reference system.
	protected VodJdoFacade _jdo;
	
	public void setUp() {
		_jdo = VodJdo.createInstance(_vod);
		cleanDb();
		produceProvider();
	}

	protected void produceProvider() {
		if (_provider != null) {
			return;
		}
		_provider = new VodReplicationProvider(_vod);
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
		
		Collection allObjects = _jdo.query(Object.class);
		for (Object object : allObjects) {
			_jdo.delete(object);
		}
		_jdo.commit();
		
		if(EXPOSE_OBJECT_DELETE_BUG){
			return;
		}
		
		// TODO: The code below shouldn't be needed but somehow
		//       querying for Object.class doesn't always work.
		
		Class[] deleteClasses = new Class[]{
				Item.class,
		};
		
		for (int i = 0; i < deleteClasses.length; i++) {
			_jdo.deleteAll(deleteClasses[i]);
			_jdo.commit();
		}
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
