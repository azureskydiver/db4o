/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.Set;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.foundation.*;
import com.db4o.util.IOServices.ProcessRunner;

public abstract class VodEventTestCaseBase extends VodProviderTestCaseBase{
	
	protected void withEventProcessor(Closure4<Void> closure) throws Exception {
		if(DrsDebug.runEventListenerEmbedded){
			withEventProcessorInSameProcess(closure);
		} else {
			withEventProcessorInSeparateProcess(closure);	
		}
	}
	
	private void withEventProcessorInSeparateProcess (Closure4<Void> closure) throws Exception {
		final ProcessRunner eventListenerProcess = _vod.startEventProcessorInSeparateProcess();
		produceProvider();
		try{
			closure.run();
		} finally {
			destroyProvider();
			eventListenerProcess.destroy();
		}
	}
	
	private void withEventProcessorInSameProcess (Closure4<Void> closure) throws Exception {
		ObjectLifecycleMonitorSupport support = new ObjectLifecycleMonitorSupport(_vod.eventConfiguration());
		produceProvider();
		try {
			closure.run();
		}
		finally {
			destroyProvider();
			support.stop();
		}
	}
}
