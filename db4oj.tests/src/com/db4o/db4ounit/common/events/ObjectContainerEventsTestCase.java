/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.events;

import com.db4o.events.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ObjectContainerEventsTestCase extends AbstractDb4oTestCase {
	private static class EventFlag {
		public boolean eventOccurred = false;
	}
	
	public void testClassRegistrationEvents() throws Exception {	
		final EventFlag eventFlag = new EventFlag();
		eventRegistry().closing().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				eventFlag.eventOccurred = true;
			}
		});
		fixture().close();
		Assert.isTrue(eventFlag.eventOccurred);
	}


}
