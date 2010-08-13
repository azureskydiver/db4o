package com.db4o.drs.versant.ipc;

import com.db4o.foundation.*;

public interface EventProcessorSideCommunication {
	void registerIsolationRequestListener(Procedure4<Integer> listener);
	void registerSyncRequestListener(Procedure4<Long> listener);
	void acknowledgeClassMetadataRegistration(String fullyQualifiedName);
	void acknowledgeIsolationMode(int isolationMode);
	void sendTimestamp(long timeStamp);
	void shutdown();
}
