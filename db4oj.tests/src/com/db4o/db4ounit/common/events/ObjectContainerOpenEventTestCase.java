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

	public void test() {
		final BooleanByRef eventReceived = new BooleanByRef(false);
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new MemoryStorage());
		config.common().add(new ConfigurationItem() {
			public void prepare(Configuration configuration) {
			}
			
			public void apply(InternalObjectContainer container) {
				EventRegistryFactory.forObjectContainer(container).opened().addListener(new EventListener4<ObjectContainerEventArgs>() {
					public void onEvent(Event4<ObjectContainerEventArgs> event, ObjectContainerEventArgs args) {
						eventReceived.value = true;
					}
				});
			}
		});
		Assert.isFalse(eventReceived.value);
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, "");
		Assert.isTrue(eventReceived.value);
		db.close();
	}
	
}
