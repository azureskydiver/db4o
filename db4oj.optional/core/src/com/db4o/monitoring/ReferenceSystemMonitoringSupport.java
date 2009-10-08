/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.monitoring;

import java.util.*;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.*;
import com.db4o.internal.references.*;
import com.db4o.internal.references.ReferenceSystem;
import com.db4o.monitoring.internal.*;

/**
 * Publishes statistics about the ReferenceSystem to JMX.
 */
@decaf.Ignore
public class ReferenceSystemMonitoringSupport implements ConfigurationItem {

	private final static class MonitoringSupportReferenceSystemFactory implements
			ReferenceSystemFactory {
		
		private final HashMap<String, com.db4o.monitoring.ReferenceSystem> _mBeans = new HashMap<String, com.db4o.monitoring.ReferenceSystem>();
		
		public ReferenceSystem newReferenceSystem(InternalObjectContainer container) {
			return new MonitoringReferenceSystem(mBeanFor(container));
		}

		private ReferenceSystemListener mBeanFor(InternalObjectContainer container) {
			com.db4o.monitoring.ReferenceSystem mBean = _mBeans.get(container.toString());
			if(mBean == null){
				mBean = Db4oMBeans.newReferenceSystemMBean(container);
				addClosingListener(container, mBean);
				_mBeans.put(container.toString(), mBean);
			}
			return mBean;
		}

		private void addClosingListener(final InternalObjectContainer container,
				final com.db4o.monitoring.ReferenceSystem referenceSystem) {
			final EventRegistry events = EventRegistryFactory.forObjectContainer(container);
			events.closing().addListener(new EventListener4<ObjectContainerEventArgs>() {
				public void onEvent(Event4<ObjectContainerEventArgs> e, ObjectContainerEventArgs args) {
					referenceSystem.unregister();
					_mBeans.remove(container.toString());
				}
			});
		}
	}

	public void apply(InternalObjectContainer container) {
		
	}

	public void prepare(Configuration configuration) {
		((Config4Impl)configuration).referenceSystemFactory(new MonitoringSupportReferenceSystemFactory());
	}

}
