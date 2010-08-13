/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.foundation.*;
import com.db4o.util.IOServices.*;

import db4ounit.*;

public class VodEventTestCaseBase extends VodProviderTestCaseBase{
	
	protected void withEventProcessor(Closure4<Void> closure, String expectedOutput) throws Exception {
		if(DrsDebug.runEventListenerEmbedded){
			withEventProcessorInSameProcess(closure, expectedOutput);
		} else {
			withEventProcessorInSeparateProcess(closure, expectedOutput);	
		}
	}
	
	private void withEventProcessorInSeparateProcess (Closure4<Void> closure, final String expectedOutput) throws Exception {
		final ProcessRunner eventListenerProcess = _vod.startEventProcessorInSeparateProcess();
		try{
			closure.run();
			boolean result = Runtime4.retry(10000, new Closure4<Boolean>() {
				public Boolean run() {
					return eventListenerProcess.outputContains(expectedOutput);
				}
			});
			Assert.isTrue(result, "Output does not contain '" + expectedOutput + "'"); 
		} finally {
			eventListenerProcess.destroy();
		}
	}
	
	private void withEventProcessorInSameProcess (Closure4<Void> closure, final String expectedOutput) throws Exception {
		EventProcessorSupport support = new EventProcessorSupport(_vod.eventConfiguration());
		try {
			closure.run();
		}
		finally {
			support.waitForOutput(expectedOutput);
			support.stop();
		}
	}
	
	protected void withEventProcessor(Closure4<Void> closure) throws Exception {
		withEventProcessor(closure, EventProcessor.LISTENING_MESSAGE);
	}

}
