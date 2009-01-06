/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import com.db4o.config.*;
import com.db4o.events.*;

import db4ounit.*;

public class InstantiationEventsTestCase extends EventsTestCaseBase {

	protected void configure(Configuration config) {
		config.activationDepth(0);
	}
	
	public void testInstantiationEvents() {
		
		final EventLog instantiatedLog = new EventLog();
		
		eventRegistry().instantiated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				assertClientTransaction(args);
				
				instantiatedLog.xed = true;
				Object obj = ((ObjectEventArgs)args).object();
				Assert.isNotNull(trans().referenceSystem().referenceForObject(obj));
			}
		});
		
		retrieveOnlyInstance(Item.class);
		
		Assert.isFalse(instantiatedLog.xing);
		Assert.isTrue(instantiatedLog.xed);
	}
}
