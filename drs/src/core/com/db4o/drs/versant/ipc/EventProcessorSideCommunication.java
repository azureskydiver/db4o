package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;

public interface EventProcessorSideCommunication {
	void registerClassMetadataRegistrationListener(Procedure4<ClassMetadata> listener);
	void registerIsolationRequestListener(Procedure4<IsolationMode> listener);
	void registerSyncRequestListener(Block4 listener);
	void acknowledgeClassMetadataRegistration(ClassMetadata classMetadata);
	void acknowledgeIsolationMode(IsolationMode isolationMode);
	void sendTimestamp(long timeStamp);
}
