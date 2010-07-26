/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.drs.versant.*;

public class VodEventTestCaseBase extends VodProviderTestCaseBase{
	
	protected static final String EVENT_LOGFILE_NAME = "DrsEventLogFile.log";
	
	protected static final int CLIENT_PORT = 4001;
	
	protected static final int SERVER_PORT = 4002;
	
	protected EventConfiguration newEventConfiguration() {
		return new EventConfiguration(DATABASE_NAME, EVENT_LOGFILE_NAME,  "localhost", SERVER_PORT, "localhost", CLIENT_PORT, true);
	}
	
	protected VodEventDriver startEventDriver() {
		VodEventDriver eventDriver = new VodEventDriver(newEventConfiguration());
		boolean started = eventDriver.start();
		if(! started ){
			eventDriver.printStartupFailure();
			throw new IllegalStateException();
		}
		return eventDriver;
	}

}
