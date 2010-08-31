/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import com.db4o.drs.versant.*;
import com.db4o.foundation.*;
import com.versant.event.*;

public class ObjectLifecycleMonitorFactory {
	
	public static ObjectLifecycleMonitorImpl newInstance (EventConfiguration eventConfiguration) {
		VodEventClient client = new VodEventClient(eventConfiguration, new ExceptionListener (){
	        public void exceptionOccurred (Throwable exception){
	        	ObjectLifecycleMonitorImpl.unrecoverableExceptionOccurred(exception);
	        }
	    });
		VodDatabase vod = new VodDatabase(eventConfiguration.databaseName);
		ObjectLifecycleMonitorImpl eventProcessor = new ObjectLifecycleMonitorImpl(client, vod);
		return eventProcessor;
	}

}
