/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

public class VodEventProcessorEnabledTestCaseBase extends VodProviderTestCaseBase {
	
	private EventProcessorSupport _eventProcessorSupport;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		_eventProcessorSupport = new EventProcessorSupport(newEventConfiguration());
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		_eventProcessorSupport.stop();
	}

}
