/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import com.db4o.drs.versant.*;
import com.versant.event.*;

public class ObjectLifecycleMonitorFactory {
	
	public static ObjectLifecycleMonitorImpl newInstance (EventConfiguration eventConfiguration) {
		VodCobraFacade cobra = VodCobra.createInstance(new VodDatabase(eventConfiguration.databaseName));
		VodEventClient client = new VodEventClient(eventConfiguration, new ExceptionListener (){
	        public void exceptionOccurred (Throwable exception){
	        	ObjectLifecycleMonitorImpl.unrecoverableExceptionOccurred(exception);
	        }
	    });
		ObjectLifecycleMonitorImpl eventProcessor = new ObjectLifecycleMonitorImpl(client, cobra);
		return eventProcessor;
	}

}
