package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.metadata.*;
import com.db4o.rmi.test.*;

public interface ObjectLifecycleMonitor {

	/**
	 * <p>
	 * Requires the EventProcessor to stop processing
	 * {@link ObjectLifecycleEvent}s in order to replicate state between the two
	 * peers.
	 * <p>
	 * It will returns <code>true</code> if the this call actually changed the
	 * isolation mode, <code>false</code> in case the isolation mode requested
	 * is already the current one.
	 * 
	 * @param isolated
	 *            <code>true</code> to start queueing
	 *            {@link ObjectLifecycleEvent}, <code>false</code> to resume
	 *            processing them.
	 * @return whether or not the isolation mode was affected by the request.
	 */
	boolean requestIsolation(boolean isolated);

	long requestTimestamp();

	void syncTimestamp(long timestamp);

	void ensureMonitoringEventsOn(String fullyQualifiedName, String schemaName, long classLoid);

	void ping();

	void stop();
	
	void addListener(@Proxy @Async MonitorListener listener);
	
	public interface MonitorListener {
		
		void ready();

		void commited();

	}

	
}
