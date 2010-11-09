/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.test.*;
import com.db4o.drs.versant.*;

public class VodDrsFixture implements DrsProviderFixture{
	
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
		
		Set<Package> packages = new HashSet<Package>();
		for (Class clazz : DrsTestCase.mappings) {
			Package p = clazz.getPackage();
			if (!packages.add(p)) {
				continue;
			}
			_vod.addJdoMetaDataFile(p);
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
