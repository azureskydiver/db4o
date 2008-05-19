/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.events;

import com.db4o.config.Configuration;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventException;
import com.db4o.events.EventListener4;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.OptOutSolo;

public class DeletionEventExceptionTestCase extends EventsTestCaseBase implements OptOutSolo {
	
	public static void main(String[] args) {
		new DeletionEventExceptionTestCase().runAll();
	}
	
	protected void configure(Configuration config) {
		config.activationDepth(1);
	}
	
	public void testDeletionEvents() {
		serverEventRegistry().deleting().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				throw new RuntimeException();
			}
		});
		final Object item = retrieveOnlyInstance(Item.class);
	    if(isMTOC()){
	        Assert.expect( EventException.class, new CodeBlock() {
                public void run() throws Throwable {
                    db().delete(item);
                }
            });
	    }else{
	        db().delete(item);
	    }
        db().commit();
	}
}
