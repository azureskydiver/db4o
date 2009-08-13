/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import javax.management.*;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.internal.query.*;

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
		
		container.getNativeQueryHandler().addListener(new Db4oQueryExecutionListener() {
			public void notifyQueryExecuted(NQOptimizationInfo info) {
				if (info.message().equals(NativeQueryHandler.UNOPTIMIZED)) {
					queries.notifyUnoptimized(info.predicate());
				}
			}
		});
		
		EventRegistryFactory.forObjectContainer(container).closing().addListener(new EventListener4<ObjectContainerEventArgs>() {
			public void onEvent(Event4<ObjectContainerEventArgs> e, ObjectContainerEventArgs args) {
				queries.unregister();
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
