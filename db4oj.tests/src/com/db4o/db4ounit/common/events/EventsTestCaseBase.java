package com.db4o.db4ounit.common.events;

import com.db4o.events.*;

import db4ounit.extensions.*;

public class EventsTestCaseBase extends AbstractDb4oTestCase {

	public static final class Item {	
	}
	
	protected static final class EventLog {
		public boolean xing;
		public boolean xed;
	}
	
	protected void store() throws Exception {
		store(new Item());
	}

	protected EventRegistry eventRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}

}