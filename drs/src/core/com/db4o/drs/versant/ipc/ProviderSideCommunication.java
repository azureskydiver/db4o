package com.db4o.drs.versant.ipc;


public interface ProviderSideCommunication {
	void requestIsolation(int isolationMode);
	long requestTimestamp();
	void syncTimestamp(long timestamp);
	void waitForClassMetadataAcknowledgment(String fullyQualifiedName);
}
