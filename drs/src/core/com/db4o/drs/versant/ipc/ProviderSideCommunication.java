package com.db4o.drs.versant.ipc;


public interface ProviderSideCommunication {
	void requestIsolation(boolean isolated);
	long requestTimestamp();
	void syncTimestamp(long timestamp);
	void ensureMonitoringEventsOn(String fullyQualifiedName, String schemaName);
}
