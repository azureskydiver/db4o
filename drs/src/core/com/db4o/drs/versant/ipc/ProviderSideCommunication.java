package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.metadata.*;

public interface ProviderSideCommunication {
	void requestIsolation(IsolationMode isolationMode);
	long requestTimestamp();
	void syncTimestamp(long timestamp);
	void waitForClassMetadataAcknowledgment(String fullyQualifiedName);
}
