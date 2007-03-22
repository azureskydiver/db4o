/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.Cool;
import com.db4o.internal.*;

import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.fixtures.*;


public class CommitTimeCallbacksForClientServerTestCase extends AbstractDb4oTestCase implements OptOutSolo {
	
	public static final class Item {
	}
	
	public static void main(String[] arguments) {
		new CommitTimeCallbacksForClientServerTestCase().runClientServer();
	}

	public void testCommittingIsTriggeredOnServer() {
		
		final EventRecorder clientRecorder = new EventRecorder(fixture().db().lock());
		clientRegistry().committing().addListener(clientRecorder);
		
		final EventRecorder serverRecorder = new EventRecorder(fileSession().lock());
		serverRegistry().committing().addListener(serverRecorder);		
		
		final Item item = new Item();
		final ExtObjectContainer client = db();
		client.set(item);
		client.commit();
		
		Cool.sleepIgnoringInterruption(50);
		
		EventAssert.assertCommitEvent(serverRecorder, serverRegistry().committing(), new ObjectInfo[] { infoFor(item) }, new ObjectInfo[0], new ObjectInfo[0]);
		EventAssert.assertNoEvents(clientRecorder);
		
	}
	
	private ObjectInfo infoFor(Object obj){
		int id = (int) db().getID(obj);
		return new LazyObjectReference(trans(), id);
	}

	private EventRegistry serverRegistry() {
		return EventRegistryFactory.forObjectContainer(fileSession());
	}

	private EventRegistry clientRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
}
