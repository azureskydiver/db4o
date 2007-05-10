package com.db4o.db4ounit.common.events;

import com.db4o.events.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.util.CrossPlatformServices;

public class ClassRegistrationEventsTestCase extends AbstractDb4oTestCase {

	public static class Data {
	}

	private static class EventFlag {
		public boolean _eventOccurred = false;
	}
	
	public void testClassRegistrationEvents() {	
		final EventFlag eventFlag = new EventFlag();
		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());
		
		registry.classRegistered().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ClassEventArgs classEventArgs = (ClassEventArgs) args;
				Assert.areEqual(
						Data.class.getName(),
						CrossPlatformServices.simpleName(
								classEventArgs.classMetadata().getName()));
				eventFlag._eventOccurred = true;
			}
		});
		store(new Data());		
		Assert.isTrue(eventFlag._eventOccurred);
	}

}
