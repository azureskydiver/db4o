package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;

public interface EventProcessorSideCommunication {
	void registerIsolationRequestListener(Procedure4<IsolationMode> listener);
	void registerSyncRequestListener(Procedure4<Long> listener);
	void acknowledgeClassMetadataRegistration(String fullyQualifiedName);
	void acknowledgeIsolationMode(IsolationMode isolationMode);
	void sendTimestamp(long timeStamp);
	void shutdown();
}
