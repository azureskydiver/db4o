/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import javax.management.*;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.query.*;

@decaf.Ignore
public class NativeQueryMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {	
		final NativeQueries queries = newNativeQueriesMBean(container);
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

	private NativeQueries newNativeQueriesMBean(InternalObjectContainer container) {
		try {
			return new NativeQueries(Db4oMBeans.mBeanNameFor(NativeQueriesMBean.class, container.toString()));
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public void prepare(Configuration configuration) {

	}

}
