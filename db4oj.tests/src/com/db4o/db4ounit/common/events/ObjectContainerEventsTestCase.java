/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.events;

import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ObjectContainerEventsTestCase extends AbstractDb4oTestCase {
	
	public void testClose() throws Exception {
		
		final ExtObjectContainer container = db();
		final LocalObjectContainer session = fileSession(); 
		final Collection4 actual = new Collection4();
		eventRegistry().closing().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				actual.add(((ObjectContainerEventArgs)args).objectContainer());
			}
		});
		fixture().close();
		
		if (isEmbeddedClientServer()) {
			Iterator4Assert.areEqual(new Object[] { container, session }, actual.iterator());
		} else {
			Assert.areSame(container, actual.singleElement());
		}
	}
}
