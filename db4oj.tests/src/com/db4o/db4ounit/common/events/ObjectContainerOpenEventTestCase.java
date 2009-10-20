/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public class ObjectContainerOpenEventTestCase implements TestCase {


	private final class OpenListenerConfigurationItem implements
			ConfigurationItem {
		private final BooleanByRef eventReceived;

		private OpenListenerConfigurationItem(BooleanByRef eventReceived) {
			this.eventReceived = eventReceived;
		}

		public void prepare(Configuration configuration) {
		}

		public void apply(InternalObjectContainer container) {
			EventRegistryFactory.forObjectContainer(container).opened().addListener(new OpenEventListener(eventReceived));
		}
	}

	private final class OpenEventListener implements EventListener4<ObjectContainerEventArgs> {
		private final BooleanByRef eventReceived;

		private OpenEventListener(BooleanByRef eventReceived) {
			this.eventReceived = eventReceived;
		}

		public void onEvent(Event4<ObjectContainerEventArgs> event, ObjectContainerEventArgs args) {
			eventReceived.value = true;
		}
	}

	public void test() {
		final BooleanByRef eventReceived = new BooleanByRef(false);
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new MemoryStorage());
		config.common().add(new OpenListenerConfigurationItem(eventReceived));
		Assert.isFalse(eventReceived.value);
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, "");
		Assert.isTrue(eventReceived.value);
		db.close();
	}
	
}
