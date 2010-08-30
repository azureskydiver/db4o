/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;

public class VodDrsFixture implements DrsFixture{
	
	private static boolean enhanced = false;
	
	private final VodDatabase _vod;
	
	protected VodReplicationProvider _provider;

	public VodDrsFixture(String name){
		_vod = new VodDatabase(name);
		_vod.removeDb();
		_vod.produceDb();
		if(! enhanced ){
			File root = new File("bin");
			
			JdoMetadataGenerator generator = new JdoMetadataGenerator(root);
			
			// TODO: Knowledge about all the persistent classes right
			// now is in DrsTestCase.mappings
			// Move to a smarter place and pull all the package names
			// from there to generate .jdo files for all of them.
			
			_vod.addJdoMetaDataFile(generator.resourcePath(generator.generate("com.db4o.drs.test.data")));
			
			_vod.enhance();
			_vod.createEventSchema();
			enhanced = true;
		}
		_vod.startEventDriver();
		
		
		new VodJdo(_vod).close();
		
		_vod.startEventProcessor();
	}
	
	public void clean() {
		// TODO: Do we need to delete all persistent instances here?
		//       It looks like this is already done in DrsTestCase#clean()
	}
	
	public void close() {
		_provider.destroy();
		_provider = null;
	}

	public void open() {
		VodCobra cobra = new VodCobra(_vod);
		ObjectLifecycleMonitor comm = ObjectLifecycleMonitorNetworkFactory.newClient(cobra, VodReplicationProvider.class.hashCode());
		_provider = new VodReplicationProvider(_vod, cobra, comm);
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + _vod;
	}
	
	public void destroy(){
		_vod.stopEventProcessor();
		_vod.stopEventDriver();
		_vod.removeDb();
	}

}
