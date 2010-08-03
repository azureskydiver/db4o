/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;

import db4ounit.*;

public class VodProviderTestCaseBase  implements TestLifeCycle, ClassLevelFixtureTest  {
	
	protected static final String EVENT_LOGFILE_NAME = "DrsEventLogFile.log";
	
	protected static final int CLIENT_PORT = 4009;
	
	protected static final int SERVER_PORT = 4010;
	
	private boolean EXPOSE_OBJECT_DELETE_BUG = false;
	
	protected static final String DATABASE_NAME = "VodProviderTestCaseBase";
	
	protected VodDatabase _vod;
	
	protected VodReplicationProvider _provider;
	
	// This is a direct _VodJdo connection that works around our _provider 
	// so we can see what's committed, using a second reference system.
	protected VodJdo _jdo;
	
	private static VodEventDriver _eventDriver;
	
	public void setUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		_jdo = new VodJdo(_vod);
		cleanDb();
		_provider = new VodReplicationProvider(_vod);
	}

	public void tearDown() throws Exception {
		_jdo.close();
		_provider.commit();
		_provider.destroy();
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
	
	public static void classSetUp() throws Exception {
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		vod.createDb();
		vod.enhance();
		vod.createEventSchema();
		_eventDriver = startEventDriver();
	}

	public static void classTearDown() {
		_eventDriver.stop();
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		vod.removeDb();
	}
	
	private static VodEventDriver startEventDriver() {
		VodEventDriver eventDriver = new VodEventDriver(newEventConfiguration());
		boolean started = eventDriver.start();
		if(! started ){
			eventDriver.printStartupFailure();
			throw new IllegalStateException();
		}
		return eventDriver;
	}
	
	protected static EventConfiguration newEventConfiguration() {
		return new EventConfiguration(DATABASE_NAME, EVENT_LOGFILE_NAME,  "localhost", SERVER_PORT, "localhost", CLIENT_PORT, true);
	}




}
