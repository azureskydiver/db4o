/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;
import com.db4o.foundation.Cool;

import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.*;


public class CommitTimeCallbacksForClientServerTestCase extends AbstractDb4oTestCase implements OptOutSolo {
	
	public static final class Item {
	}

	public void _testCommittingIsTriggeredOnServer() {
		
		final EventRecorder clientRecorder = new EventRecorder();
		clientRegistry().committing().addListener(clientRecorder);
		
		final EventRecorder serverRecorder = new EventRecorder();		
		serverRegistry().committing().addListener(serverRecorder);
		
		final Item item = new Item();
		db().set(item);
		db().commit();
		
		Cool.sleepIgnoringInterruption(50);
		
		EventAssert.assertCommitEvent(serverRecorder, serverRegistry().committing(), new Item[] { item }, new Item[0], new Item[0]);
		EventAssert.assertNoEvents(clientRecorder);
		
	}

	private EventRegistry serverRegistry() {
		return EventRegistryFactory.forObjectContainer(fileSession());
	}

	private EventRegistry clientRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
}
