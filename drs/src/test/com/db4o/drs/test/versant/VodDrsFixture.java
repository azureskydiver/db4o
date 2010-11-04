/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;
import com.db4o.drs.versant.*;

public class VodDrsFixture implements DrsProviderFixture{
	
	private static boolean enhanced = false;
	
	private VodDatabase _vod;
	
	protected VodReplicationProvider _provider;

	private final String _name;
	
	private boolean _eventProcessingActive = false;

	public VodDrsFixture(String name){
		_name = name;
		init();
	}

	private void init() {
		_vod = new VodDatabase(_name);
		_vod.removeDb();
		_vod.produceDb();
		JdoMetadataGenerator generator = new JdoMetadataGenerator(new File("bin"));
		
		// TODO: Knowledge about all the persistent classes right
		// now is in DrsTestCase.mappings
		// Move to a smarter place and pull all the package names
		// from there to generate .jdo files for all of them.
		
		_vod.addJdoMetaDataFile(generator.resourcePath(generator.generate("com.db4o.drs.test.data")));
		if(! enhanced ){
			_vod.enhance();
			enhanced = true;
		}
		_vod.createEventSchema();
		ensureJdoMetadataCreated();
	}

	private void ensureJdoMetadataCreated() {
		VodJdo.createInstance(_vod).close();
	}
	
	public void close() {
		_provider.destroy();		
		_provider = null;
	}

	public void open() {
		ensureEventProcessing();
		_provider = new VodReplicationProvider(_vod);
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + _vod;
	}
	
	public void clean() {
		internalClean(true);
	}

	private void internalClean(boolean deleteAll) {
		if(_eventProcessingActive){
			_vod.stopEventProcessor();
			_eventProcessingActive = false;
		}
		if(deleteAll){
			VodCobra.deleteAll(_vod);
		}
	}
	
	public void destroy(){
		internalClean(false);
		_vod.stopEventDriver();
		_vod.removeDb();
	}
	
	public void ensureEventProcessing(){
		if(_eventProcessingActive){
			return;
		}
		_vod.startEventDriver();
		_vod.startEventProcessor();
		_eventProcessingActive = true;
	}
	

}
