/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.monitoring;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
@decaf.Ignore
public abstract class MonitoringSupportBase implements ConfigurationItem {

	protected static void addClosingListener(final InternalObjectContainer container, final MBeanRegistrationSupport mBean) {
		final EventRegistry events = EventRegistryFactory.forObjectContainer(container);
		events.closing().addListener(new EventListener4<ObjectContainerEventArgs>() {
			public void onEvent(Event4<ObjectContainerEventArgs> e, ObjectContainerEventArgs args) {
				mBean.unregister();
			}
		});
	}

}
