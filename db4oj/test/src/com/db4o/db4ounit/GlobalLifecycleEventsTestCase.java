package com.db4o.db4ounit;

import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;

import db4ounit.db4o.Db4oTestCase;

public class GlobalLifecycleEventsTestCase extends Db4oTestCase {
	
	public void testEventOrdering() {
		
		// canNew		
		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());
		
		//registry.objectCanNew().addListener();
		
	}

}
