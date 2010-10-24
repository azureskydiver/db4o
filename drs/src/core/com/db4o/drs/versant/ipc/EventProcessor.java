package com.db4o.drs.versant.ipc;

import java.util.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;
import com.db4o.rmi.*;

public interface EventProcessor {

	/**
	 * <p>
	 * Requires the EventProcessor to stop processing
	 * {@link ObjectInfo}s in order to replicate state between the two
	 * peers.
	 * <p>
	 * It will returns <code>true</code> if the this call actually changed the
	 * isolation mode, <code>false</code> in case the isolation mode requested
	 * is already the current one.
	 * 
	 * @param isolated
	 *            <code>true</code> to start queueing
	 *            {@link ObjectInfo}, <code>false</code> to resume
	 *            processing them.
	 * @return whether or not the isolation mode was affected by the request.
	 */
	boolean requestIsolation(boolean isolated);
	
	long generateTimestamp();

	long lastTimestamp();

	void syncTimestamp(long timestamp);

	void ping();

	void stop();
	
	void addListener(@Proxy @Async EventProcessorListener listener);
	
	void removeListener(@Proxy EventProcessorListener listener);
	
	public interface EventProcessorListener {
		
		void ready();

		void committed(String transactionId);
		
		void onEvent(long loid);

	}

	Map<String, Long> ensureMonitoringEventsOn(String className);
	
	void forceTimestampsAndSignatures(List<Pair<Long, Long>> loidTimeStamps, List<LoidSignatureLongPart> loidSignatureLongParts);
	
	long defaultSignatureLoid();


}
