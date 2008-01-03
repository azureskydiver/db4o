/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class CommittingCallbacksForClientServerTestCase extends AbstractDb4oTestCase implements OptOutSolo {
	
	public static final class Item {
	}
	
	public static void main(String[] arguments) {
		new CommittingCallbacksForClientServerTestCase().runClientServer();
	}
	
	
	public void testTriggerCommitting() {
		
		final EventRecorder clientRecorder = new EventRecorder(fixture().db().lock());
		clientRegistry().committing().addListener(clientRecorder);
		
		final EventRecorder serverRecorder = new EventRecorder(fileSession().lock());
		serverEventRegistry().committing().addListener(serverRecorder);		
		
		final Item item = new Item();
		final ExtObjectContainer client = db();
		client.store(item);
		client.commit();
		
		Cool.sleepIgnoringInterruption(50);
		
		EventAssert.assertCommitEvent(serverRecorder, serverEventRegistry().committing(), new ObjectInfo[] { infoFor(item) }, new ObjectInfo[0], new ObjectInfo[0]);
	    
		// For MTOC we expect the same events, in a normal client we don't want to see these events. 
		if(isMTOC()){
		    EventAssert.assertCommitEvent(clientRecorder, serverEventRegistry().committing(), new ObjectInfo[] { infoFor(item) }, new ObjectInfo[0], new ObjectInfo[0]);
		}else{
		    EventAssert.assertNoEvents(clientRecorder);
		}
		
	}
	
	private ObjectInfo infoFor(Object obj){
		int id = (int) db().getID(obj);
		return new LazyObjectReference(fileSession().transaction(), id);
	}

	private EventRegistry clientRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
}
