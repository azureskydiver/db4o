/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;


import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.InternalObjectContainer;
import com.db4o.internal.query.*;

/**
 * Publishes native query statistics to JMX.  
 */
@decaf.Ignore
public class NativeQueryMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {	
		final NativeQueries queries = Db4oMBeans.newNativeQueriesMBean(container);
		final EventRegistry events = EventRegistryFactory.forObjectContainer(container);
		events.closing().addListener(new EventListener4<ObjectContainerEventArgs>() {
			public void onEvent(Event4<ObjectContainerEventArgs> e, ObjectContainerEventArgs args) {
				queries.unregister();
			}
		});
		container.getNativeQueryHandler().addListener(new Db4oQueryExecutionListener() {
			public void notifyQueryExecuted(NQOptimizationInfo info) {
				queries.notifyNativeQuery(info);
			}
		});
	}

	public void prepare(Configuration configuration) {

	}

}
