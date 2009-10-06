/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import javax.management.*;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;

/**
 * @publishes statistics about Queries to JMX.
 */
@decaf.Ignore
public class QueryMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
		
		final Queries queries = newQueriesMBean(container.toString());
		final CommonConfiguration config = Db4oLegacyConfigurationBridge.asCommonConfiguration(container.configure());
		config.diagnostic().addListener(new DiagnosticListener() {
			public void onDiagnostic(Diagnostic d) {
				if (d instanceof LoadedFromClassIndex) {
					queries.notifyClassIndexScan((LoadedFromClassIndex)d);
				}
			}
		});
		
		final EventRegistry events = EventRegistryFactory.forObjectContainer(container);
		events.closing().addListener(new EventListener4<ObjectContainerEventArgs>() {
			public void onEvent(Event4<ObjectContainerEventArgs> e, ObjectContainerEventArgs args) {
				queries.unregister();
			}
		});
		events.queryStarted().addListener(new EventListener4<QueryEventArgs>() {
			public void onEvent(Event4<QueryEventArgs> e, QueryEventArgs args) {
				queries.notifyQueryStarted();
			}
		});
		
		events.queryFinished().addListener(new EventListener4<QueryEventArgs>() {
			public void onEvent(Event4<QueryEventArgs> e, QueryEventArgs args) {
				queries.notifyQueryFinished();
			}
		});
		
	}

	private Queries newQueriesMBean(String uri) {
		try {
			return new Queries(Db4oMBeans.mBeanNameFor(QueriesMBean.class, uri));
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public void prepare(Configuration configuration) {
	}

}
